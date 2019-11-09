package com.adamratzman.geicobot.commands

import com.adamratzman.geicobot.spotify.currentlyPlaying
import com.adamratzman.geicobot.spotify.fancyString
import com.adamratzman.geicobot.spotify.getSpotifyApi
import com.adamratzman.geicobot.spotify.getUser
import com.adamratzman.geicobot.system.AutowiredCommand
import com.adamratzman.geicobot.system.Category
import com.adamratzman.geicobot.system.Command
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse
import spark.Session

/*
@AutowiredCommand
class Play : Command(Category.MUSIC, "play", "play songs, playlists, or your personal music library!", "p") {
    override fun executeBase(arguments: MutableList<String>, event: MessageReceivedEvent) {
        if (arguments.size == 0) {
            event.channel.send("You can search or play single tracks from Youtube or Spotify by typing *play [search or url]*.")
        } else {
            arguments.concat().load(event.member!!, event.textChannel)
        }
    }
}*/

@AutowiredCommand
class PlayFavorites : Command(Category.MUSIC, "playfavorites", "play your favorite tracks", "lex.playfavorites") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val api = session.getSpotifyApi()
        val user = session.getUser()
        api.player.startPlayback(tracksToPlay = *user.favoriteTracks.map { it.first }.toTypedArray())
            .queue({ consumer("Added your favorite tracks (${user.favoriteTracks.size}) to the queue") },
                { consumer("Failed to play: ${it.message}") })
    }
}

@AutowiredCommand
class Pause : Command(Category.MUSIC, "pause", "pause Spotify playback", "lex.pausetrack") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val api = session.getSpotifyApi()
        api.player.pause()
            .queue({ consumer("Paused ${api.currentlyPlaying()?.track?.fancyString() ?: "unknown track"}!") },
                { consumer("Failed to pause playback: ${it.message}") })
    }
}

@AutowiredCommand
class Resume : Command(Category.MUSIC, "resume", "resume Spotify playback", "lex.resumetrack") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val api = session.getSpotifyApi()
        api.player.resume().queue({
            consumer(
                "Resumed playback of ${api.player.getCurrentlyPlaying().complete()?.track?.name ?: "unknown track"}!"
            )
        },
            { consumer("Failed to resume playback: ${it.message}") })
    }
}

@AutowiredCommand
class Skip : Command(Category.MUSIC, "skip", "skip the current Spotify song", "lex.skiptrack") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val api = session.getSpotifyApi()
        val currentTrack = api.player.getCurrentlyPlaying().complete()?.track?.name ?: "unknown track"
        api.player.skipForward().queue({ consumer("Skipped $currentTrack") },
            { consumer("Failed to skip: ${it.message}") })
    }
}

@AutowiredCommand
class Restart : Command(Category.MUSIC, "restart", "restart the current Spotify song", "lex.restarttrack") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val api = session.getSpotifyApi()
        val currentTrack = api.player.getCurrentlyPlaying().complete()?.track?.name ?: "unknown track"
        api.player.seek(0).queue({ consumer("Restarted $currentTrack") },
            { consumer("Failed to restart: ${it.message}") })
    }
}

@AutowiredCommand
class PreviousSong : Command(Category.MUSIC, "previoussong", "play the previous song", "lex.previoussong") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val api = session.getSpotifyApi()
        api.player.skipBehind().queue({ consumer("Starting to play the previous song..") },
            { consumer("Failed to restart: ${it.message}") })
    }
}

@AutowiredCommand
class MoveToTime :
    Command(Category.MUSIC, "movetotime", "move to a different time (in seconds) in the track", "lex.movetotime") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val time = input.replace("seconds", "").trim().toLongOrNull()
        if (time == null) consumer("You need to put in a time!")
        else {
            val api = session.getSpotifyApi()
            api.player.seek(time * 1000).queue({ consumer("Starting playback at $time seconds..") },
                { consumer("Failed to restart: ${it.message}") })
        }
    }
}

@AutowiredCommand
class CurrentlyPlaying :
    Command(Category.MUSIC, "currentlyplaying", "see what song is currently playing", "lex.currentlyplaying") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val api = session.getSpotifyApi()
        api.player.getCurrentlyPlaying().complete()?.track?.fancyString()?.let {
            consumer("$it")
        } ?: consumer("There's no currently playing track")
    }
}

@AutowiredCommand
class FindTrack : Command(Category.MUSIC, "findtrack", "find a track", "lex.findtrack") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        if (input.isBlank()) consumer("You provided an invalid track name")
        else {
            val api = session.getSpotifyApi()
            val searchItems = api.search.searchTrack(input.trim(), limit = 3).complete().items
            if (searchItems.isEmpty()) consumer("No track found by that name")
            else {
                var sb = "Found ${searchItems.size} tracks"
                searchItems.forEach { track ->
                    sb += "\n<a target='_blank' href='/track/${track.id}'>${track.fancyString()}</a>"
                }

                consumer(sb)
            }
        }
    }
}


@AutowiredCommand
class FindAlbum : Command(Category.MUSIC, "findalbum", "find an album", "lex.findalbum") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        if (input.isBlank()) consumer("You provided an invalid album name")
        else {
            val api = session.getSpotifyApi()
            val searchItems = api.search.searchAlbum(input.trim(), limit = 3).complete().items
            if (searchItems.isEmpty()) consumer("No album found by that name")
            else {
                var sb = "Found ${searchItems.size} albums"
                searchItems.forEach { album ->
                    sb += "\n<a target='_blank' href='/album/${album.id}'>${album.fancyString()}</a>"
                }

                consumer(sb)
            }
        }
    }
}

@AutowiredCommand
class FindPlaylist : Command(Category.MUSIC, "findplaylist", "find a playlist", "lex.findplaylist") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        if (input.isBlank()) consumer("You provided an invalid playlist name")
        else {
            val api = session.getSpotifyApi()
            val searchItems = api.search.searchPlaylist(input.trim(), limit = 3).complete().items
            if (searchItems.isEmpty()) consumer("No playlist found by that name")
            else {
                var sb = "Found ${searchItems.size} playlists"
                searchItems.forEach { playlist ->
                    sb += "\n<a target='_blank' href='/playlist/${playlist.id}'>${playlist.fancyString()}</a>"
                }

                consumer(sb)
            }
        }
    }
}


@AutowiredCommand
class FindArtist : Command(Category.MUSIC, "findartist", "find an artist", "lex.findartist") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        if (input.isBlank()) consumer("You provided an invalid artist name")
        else {
            val api = session.getSpotifyApi()
            val searchItems = api.search.searchArtist(input.trim(), limit = 3).complete().items
            if (searchItems.isEmpty()) consumer("No artist found by that name")
            else {
                var sb = "Found ${searchItems.size} artists"
                searchItems.forEach { artist ->
                    sb += "\n<a target='_blank' href='/artist/${artist.id}'>${artist.name}</a>"
                }

                consumer(sb)
            }
        }
    }
}

@AutowiredCommand
class TopArtists : Command(Category.MUSIC, "topartists", "see your top artists", "lex.topartists") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val api = session.getSpotifyApi()
        val searchItems = api.personalization.getTopArtists(limit = 5).complete().items
        if (searchItems.isEmpty()) consumer("No top artists found")
        else {
            var sb = "Your top ${searchItems.size} artists"
            searchItems.forEach { artist ->
                sb += "\n<a target='_blank' href='/artist/${artist.id}'>${artist.name}</a>"
            }

            consumer(sb)
        }
    }
}

@AutowiredCommand
class TopTracks : Command(Category.MUSIC, "toptracks", "see your top songs", "lex.toptracks") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val api = session.getSpotifyApi()
        val searchItems = api.personalization.getTopTracks(limit = 5).complete().items
        if (searchItems.isEmpty()) consumer("No top tracks found")
        else {
            var sb = "Your top ${searchItems.size} tracks"
            searchItems.forEach { track ->
                sb += "\n<a target='_blank' href='/track/${track.id}'>${track.fancyString()}</a>"
            }

            consumer(sb)
        }
    }
}