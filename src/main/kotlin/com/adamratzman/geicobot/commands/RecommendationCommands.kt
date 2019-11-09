package com.adamratzman.geicobot.commands

import com.adamratzman.geicobot.db.getUser
import com.adamratzman.geicobot.db.update
import com.adamratzman.geicobot.spotify.fancyString
import com.adamratzman.geicobot.spotify.getSpotifyApi
import com.adamratzman.geicobot.spotify.getUser
import com.adamratzman.geicobot.spotify.spotifyApi
import com.adamratzman.geicobot.system.AutowiredCommand
import com.adamratzman.geicobot.system.Category
import com.adamratzman.geicobot.system.Command
import com.adamratzman.spotify.SpotifyClientAPI
import com.adamratzman.spotify.endpoints.public.TrackAttribute
import com.adamratzman.spotify.endpoints.public.TuneableTrackAttribute
import com.adamratzman.spotify.models.*
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse
import spark.Session

@AutowiredCommand
class RecommendSong : Command(
    Category.MUSIC,
    "recommendsong",
    "I'll recommend a song to you based on your past listening habits",
    "lex.recommendsong"
) {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val tracks = recommend(session, session.getSpotifyApi().getUserData())

        if (tracks.isEmpty()) consumer("No recommendations found.")
        else {
            var sb = "I think you might like these tracks"
            tracks.take(3).forEach { track ->
                sb += "\n<a target='_blank' href='/track/${track.id}'>${track.fancyString()}</a>"
            }
            consumer(sb)
        }

    }
}

fun SpotifyClientAPI.getUserData(): SpotifyUserData {
    return SpotifyUserData(
        userId,
        personalization.getTopTracks().complete().items.toMutableList(),
        personalization.getTopArtists().complete().items.toMutableList(),
        users.getUserProfile().complete(),
        following.getFollowedArtists().complete().items.toMutableList()
    )
}

data class SpotifyUserData(
    val userId: String, val topTracks: MutableList<Track>,
    val topArtists: MutableList<Artist>, val profile: SpotifyUserInformation,
    val followedArtists: MutableList<Artist>
) {
    var currentMood: String? = null
        get() = field?.toLowerCase()
    var currentActivity: String? = null
        get() = field?.toLowerCase()

    val mlGeneratedArtistIds = mutableListOf<String>()

    var listenNew = false

    fun orderByFavoriteGenres(): List<Pair<String, Int>> {
        return topArtists.asSequence().map { it.genres }.flatten().distinct()
            .map { genre -> genre to topArtists.count { it.genres.contains(genre) } }
            .sortedByDescending { it.second }.toList()
    }
}

fun recommend(
    session: Session,
    userData: SpotifyUserData,
    genreIds: List<String> = listOf(),
    trackIds: List<String> = listOf(),
    artistIds: List<String> = listOf(),
    trackAttributes: List<TrackAttribute<*>> = listOf(),
    market: Market? = null
): List<SimpleTrack> {
    val attributes = trackAttributes.toMutableList() +
            when {
                userData.currentMood?.contains("happy") == true && userData.currentActivity?.contains("study") == true ->
                    listOf(
                        TuneableTrackAttribute.TEMPO.asTrackAttribute(.75f),
                        TuneableTrackAttribute.VALENCE.asTrackAttribute(.9f),
                        TuneableTrackAttribute.ENERGY.asTrackAttribute(.6f),
                        TuneableTrackAttribute.DANCEABILITY.asTrackAttribute(.2f)
                    )

                userData.currentMood?.contains("sad") == true && userData.currentActivity?.contains("study") == true ->
                    listOf(
                        TuneableTrackAttribute.TEMPO.asTrackAttribute(.3f),
                        TuneableTrackAttribute.VALENCE.asTrackAttribute(.2f),
                        TuneableTrackAttribute.ENERGY.asTrackAttribute(.1f),
                        TuneableTrackAttribute.DANCEABILITY.asTrackAttribute(.2f)
                    )

                userData.currentMood?.contains("stressed") == true && userData.currentActivity?.contains("study") == true ->
                    listOf(
                        TuneableTrackAttribute.TEMPO.asTrackAttribute(.2f),
                        TuneableTrackAttribute.VALENCE.asTrackAttribute(.4f),
                        TuneableTrackAttribute.ENERGY.asTrackAttribute(.4f),
                        TuneableTrackAttribute.DANCEABILITY.asTrackAttribute(.2f)
                    )

                userData.currentMood?.contains("happy") == true && userData.currentMood?.contains("stressed") == true && userData.currentActivity?.contains(
                    "work"
                ) == true ->
                    listOf(
                        TuneableTrackAttribute.TEMPO.asTrackAttribute(.9f),
                        TuneableTrackAttribute.VALENCE.asTrackAttribute(.9f),
                        TuneableTrackAttribute.ENERGY.asTrackAttribute(1f),
                        TuneableTrackAttribute.DANCEABILITY.asTrackAttribute(.9f)
                    )

                userData.currentMood?.contains("sad") == true && userData.currentActivity?.contains("work") == true ->
                    listOf(
                        TuneableTrackAttribute.TEMPO.asTrackAttribute(.5f),
                        TuneableTrackAttribute.VALENCE.asTrackAttribute(.6f),
                        TuneableTrackAttribute.ENERGY.asTrackAttribute(.4f),
                        TuneableTrackAttribute.DANCEABILITY.asTrackAttribute(.8f)
                    )

                userData.currentActivity?.contains("party") == true ->
                    listOf(
                        TuneableTrackAttribute.TEMPO.asTrackAttribute(.6f),
                        TuneableTrackAttribute.VALENCE.asTrackAttribute(.75f),
                        TuneableTrackAttribute.ENERGY.asTrackAttribute(.8f),
                        TuneableTrackAttribute.DANCEABILITY.asTrackAttribute(1f)
                    )
                else -> listOf()
            }

    val recommendations = spotifyApi.browse.getTrackRecommendations(
        artistIds + userData.mlGeneratedArtistIds,
        if (genreIds.isEmpty() && trackIds.isEmpty() && artistIds.isEmpty()) listOf(
            userData.orderByFavoriteGenres().take(
                10
            ).map { it.first }.random()
        )
        else genreIds,
        if (genreIds.isEmpty() && trackIds.isEmpty() && artistIds.isEmpty()) listOf(userData.topTracks.take(10).random().id) else trackIds,
        targetAttributes = attributes,
        market = market
    ).complete().tracks

    val user = session.getUser()
    user.recommendations.add(recommendations.first().id to System.currentTimeMillis())

    update("users", user.id, user)

    return recommendations
}