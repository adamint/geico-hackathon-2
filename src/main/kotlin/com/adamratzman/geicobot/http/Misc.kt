package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.GeicoBot
import spark.Spark.get

fun GeicoBot.misc() {
    get("/logout") { request, response ->
        request.session().invalidate()
        response.redirect("/?logout=true")
    }
}