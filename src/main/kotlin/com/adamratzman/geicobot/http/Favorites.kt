package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.GeicoBot
import com.adamratzman.geicobot.db.update
import com.adamratzman.geicobot.spotify.assureLoggedIn
import com.adamratzman.geicobot.spotify.getSpotifyApi
import com.adamratzman.geicobot.spotify.getUser
import spark.Spark.get
import spark.Spark.path

fun GeicoBot.favorites() {
    path("/favorites") {
        get("/add/:trackId") { request, response ->
            if (!assureLoggedIn(request, response)) return@get ""

            val user = request.session().getUser()
            user.favoriteTracks.add(
                request.session().getSpotifyApi().tracks.getTrack(request.params(":trackId")).complete()!!
                        to System.currentTimeMillis()
            )

            update("users", user.id, user)

            response.redirect("/bot")
        }

        get("/remove/:trackId") { request, response ->
            if (!assureLoggedIn(request, response)) return@get ""

            val user = request.session().getUser()
            user.favoriteTracks.removeIf { it.first.id == request.params(":trackId")}

            update("users", user.id, user)

            response.redirect("/bot")
        }
    }
}