package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.GeicoBot
import com.adamratzman.geicobot.db.getUser
import com.adamratzman.geicobot.getMap
import com.adamratzman.geicobot.handlebars
import com.adamratzman.geicobot.spotify.assureLoggedIn
import com.adamratzman.geicobot.spotify.getSpotifyApi
import com.adamratzman.spotify.SpotifyClientAPI
import spark.Spark.get

fun GeicoBot.profile() {
    get("/profile") { request, response ->
        if (!assureLoggedIn(request, response)) return@get ""

        val map = getMap(request, "My Profile", "profile", false)
        map["user"] = getUser(request.session().getSpotifyApi().userId, request.session().getSpotifyApi())
        handlebars.render(map, "profiles.hbs")
    }
}