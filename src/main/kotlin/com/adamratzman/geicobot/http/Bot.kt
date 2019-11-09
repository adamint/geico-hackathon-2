package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.*
import com.adamratzman.geicobot.chat.CleverbotResponse
import com.adamratzman.geicobot.chat.SenderType
import com.adamratzman.geicobot.db.User
import com.adamratzman.geicobot.db.getUser
import com.adamratzman.geicobot.spotify.assureLoggedIn
import com.adamratzman.geicobot.spotify.getSpotifyApi
import com.github.marlonlom.utilities.timeago.TimeAgo
import spark.Spark.get
import spark.Spark.path
import java.time.Instant

fun GeicoBot.bot() {
    path("/bot") {
        get("") { request, response ->
            if (!assureLoggedIn(request, response)) return@get ""

            val map = getMap(request, "Bot", "bot", true)

            val user = map["user"] as User

            map["examples"] = listOf(
                "Play a song" to "play \"song name\"",
                "Flip a coin" to "Have GEICOBot flip a coin",
                "Tell me a fortune" to "Get a fortune from GEICOBot",
                "Recommend me a song" to "Get a song recommendation from GEICOBot",
                "Recommend me an artist" to "Get an artist recommendation from GEICOBot",
                "Find song" to "Have GEICOBot search Spotify for a song",
                "Pause Spotify" to "Pause the current song",
                "Resume Spotify" to "Resume paused song",
                "Skip song" to "Skip to the next song",
                "Restart song" to "Restart the current song",
                "Move to time" to "Move to a designated point in the current song",
                "Previous song" to "Play the previous song",
                "Add favorite" to "Adds current song to favorites",
                "View Favorites" to "View favorites list"



            )

            val api = request.session().getSpotifyApi()

            map["recentlyPlayed"] = api.player.getRecentlyPlayed(limit = 3).complete()
                .map {
                    val track = api.tracks.getTrack(it.track.id).complete()
                    arrayOf(track, track?.artists?.joinToString(", ") { it.name }, user.favoriteTracks.any { it.first.id == track?.id })
                }

            handlebars.render(map, "bot.hbs")
        }

        get("/respond") { request, response ->
            var responded = false
            val input = request.queryParams("input")

            commandFactory.onMessageEvent(input, request.session()) { output ->
                response.body(output?.replace("\n", "<br />") ?: "null")
                responded = true
            }

            while (!responded) Thread.sleep(100)
            gson.toJson(CleverbotResponse(200, response.body()))
        }

        get("/chatlogs/:userId") { request, response ->
            try {
                val user = getUser(request.params(":userId"), null)
                var sb = "Chatlogs for ${user.id}<br /><br />"

                sb += user.conversation.joinToString("<br />") { dialog ->
                    "${dialog.time.toDate()} (${TimeAgo.using(dialog.time)}) - ${if (dialog.sender == SenderType.BOT) "GEICOBot" else user.id}: ${dialog.text}"
                }

                sb
            } catch (ignored: Exception) {
                response.redirect("/")
            }
        }
    }
}