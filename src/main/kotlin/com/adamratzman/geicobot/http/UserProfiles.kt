package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.GeicoBot
import com.adamratzman.geicobot.db.getUsers
import com.adamratzman.geicobot.getMap
import com.adamratzman.geicobot.handlebars
import com.adamratzman.geicobot.spotify.assureLoggedIn
import com.adamratzman.geicobot.spotify.getSpotifyApi
import spark.Spark.get

fun GeicoBot.userProfiles() {
    get("/profiles") { request, response ->
        if (!assureLoggedIn(request, response)) return@get ""

        val map = getMap(request, "User Profiles", "profiles", true)
        val users = getUsers()
        map["users"] = users.filter { it.id != request.session().getSpotifyApi().userId }
            .sortedByDescending { it.lastActive }

        handlebars.render(map, "profiles.hbs")
    }
}