package com.adamratzman.geicobot.spotify

import com.adamratzman.geicobot.db.User
import com.adamratzman.geicobot.db.getUser
import com.adamratzman.geicobot.db.update
import com.adamratzman.spotify.SpotifyClientAPI
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.models.*
import com.adamratzman.spotify.spotifyAppApi
import com.adamratzman.spotify.spotifyClientApi
import spark.Request
import spark.Response
import spark.Session

val sId = "9e0323b3b595424fae96001d752f9638"
val sPassword = "1f8911e216a4476ca973c5a622fc15bd"

val spotifyApi = spotifyAppApi {
    credentials {
        clientId = sId
        clientSecret = sPassword
    }
}.buildPublic()

val authorizationUrl = spotifyClientApi {
    credentials {
        clientId = sId
        clientSecret = sPassword
        redirectUri = "http://localhost:8080/spotify-callback"
    }
}.getAuthorizationUrl(
    SpotifyScope.PLAYLIST_READ_PRIVATE,
    SpotifyScope.USER_LIBRARY_READ,
    SpotifyScope.USER_READ_PRIVATE, SpotifyScope.USER_TOP_READ,
    SpotifyScope.USER_FOLLOW_READ,
    SpotifyScope.STREAMING,
    SpotifyScope.USER_MODIFY_PLAYBACK_STATE,
    SpotifyScope.USER_READ_PLAYBACK_STATE,
    SpotifyScope.USER_READ_CURRENTLY_PLAYING,
    SpotifyScope.USER_READ_RECENTLY_PLAYED
)

fun isLoggedIn(request: Request): Boolean = request.session().attribute<Any?>("spotify") != null

fun assureLoggedIn(request: Request, response: Response): Boolean {
    return if (!isLoggedIn(request)) {
        response.redirect(authorizationUrl)
        false
    } else {
        val user = request.session().getUser()
        user.lastActive = System.currentTimeMillis()
        update("users", user.id, user)
        true
    }
}

fun Session.getUser(): User = getUser(attribute<String>("userId"), getSpotifyApi())
fun Session.getSpotifyApi() = attribute<SpotifyClientAPI>("spotify")

fun Track.fancyString() = "$name by ${artists.joinToString(", ") { it.name }}"
fun SimpleAlbum.fancyString() = "$name by ${artists.joinToString(", ") { it.name }}"
fun Album.fancyString() = "$name by ${artists.joinToString(", ") { it.name }}"
fun SimplePlaylist.fancyString() = "$name (${tracks.total} tracks) by ${owner.displayName?.let { "${owner.displayName} (${owner.id})" } ?: owner.id}"
fun Playlist.fancyString() = "$name (${tracks.total} tracks) by ${owner.displayName?.let { "${owner.displayName} (${owner.id})" } ?: owner.id}"

fun SpotifyClientAPI.currentlyPlaying() = this.player.getCurrentlyPlaying().complete()