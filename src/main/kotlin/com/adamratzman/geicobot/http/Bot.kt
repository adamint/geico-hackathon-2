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
                "Flip a coin" to "Have GEICObot flip a coin",
                "Tell me a fortune" to "Get a fortune from GEICObot",
                "Recommend me a song" to "Get a song recommendation from GEICObot",
                "Recommend me an artist" to "Get an artist recommendation from GEICObot",
                "Find song" to "Have GEICObot search spotify for a song",
                "Pause Spotify" to "Pause the current song",
                "Resume Spotify" to "Resume paused song",
                "Skip song" to "Skip to the next song"


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