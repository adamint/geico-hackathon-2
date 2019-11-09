package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.*
import com.adamratzman.geicobot.chat.CleverbotResponse
import com.adamratzman.geicobot.spotify.assureLoggedIn
import spark.Spark.get
import spark.Spark.path

fun GeicoBot.bot() {
    path("/bot") {
        get("") { request, response ->
            if (!assureLoggedIn(request, response)) return@get ""

            val map = getMap(request, "Bot", "bot", true)

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
                "Previous song" to "Play the previous song"


            )

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
    }
}