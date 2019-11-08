package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.GeicoBot
import com.adamratzman.geicobot.getMap
import com.adamratzman.geicobot.handlebars
import spark.Spark.get

fun GeicoBot.home() {
    get("/") { request, _ ->
        val map = getMap(request, "Home", "home", true)

        handlebars.render(map, "index.hbs")
    }
}