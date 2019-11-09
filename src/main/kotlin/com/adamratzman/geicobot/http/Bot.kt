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
                "Play a song" to "play \"song name\""
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