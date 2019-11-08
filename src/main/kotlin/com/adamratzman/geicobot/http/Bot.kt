package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.*
import com.adamratzman.geicobot.chat.CleverbotResponse
import com.adamratzman.geicobot.chat.respond
import com.adamratzman.geicobot.chat.scraper
import com.adamratzman.geicobot.getMap
import com.adamratzman.geicobot.spotify.assureLoggedIn
import spark.Spark.get
import spark.Spark.path

fun GeicoBot.bot() {
    path("/bot") {
        get("") { request, response ->
            if (!assureLoggedIn(request, response)) return@get ""

            val map = getMap(request, "Bot", "bot", true)

            map["examples"] = listOf(
                "Play a song" to "play \"song name\""
            )

            handlebars.render(map, "bot.hbs")
        }

        get("/respond") { request, response ->
            var responded = false
            val input = request.queryParams("input")

            commandFactory.onMessageEvent(input, request.session()) { output ->
                response.body(output)
                responded = true
            }

            while (!responded) Thread.sleep(100)
            gson.toJson(CleverbotResponse(200, response.body()))
        }
    }
}