package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.GeicoBot
import com.adamratzman.geicobot.db.update
import com.adamratzman.geicobot.getMap
import com.adamratzman.geicobot.handlebars
import com.adamratzman.geicobot.spotify.assureLoggedIn
import com.adamratzman.geicobot.spotify.getSpotifyApi
import com.adamratzman.geicobot.spotify.getUser
import com.adamratzman.geicobot.toDate
import spark.Spark.get
import spark.Spark.path

fun GeicoBot.favorites() {
    path("/favorites") {
        get("") { request, response ->
            if (!assureLoggedIn(request, response)) return@get ""
            val map = getMap(request, "Favorites", "favorites", true)
            val user = request.session().getUser()
            map["favorites"] = user.favoriteTracks
                .map {
                    val track = request.session().getSpotifyApi().tracks.getTrack(it.first).complete()
                    arrayOf(
                        track,
                        track?.artists?.joinToString(", ") { it.name },
                        user.favoriteTracks.any { it.first == track?.id },
                        it.second.toDate()
                    )
                }

            handlebars.render(map, "favorites.hbs")
        }

        get("/add/:trackId") { request, response ->
            if (!assureLoggedIn(request, response)) return@get ""

            val user = request.session().getUser()
            user.favoriteTracks.add(
                request.params(":trackId") to System.currentTimeMillis()
            )

            update("users", user.id, user)

            response.redirect(request.queryParams("redirect"))
        }

        get("/remove/:trackId") { request, response ->
            if (!assureLoggedIn(request, response)) return@get ""

            val user = request.session().getUser()
            user.favoriteTracks.removeIf { it.first == request.params(":trackId") }

            update("users", user.id, user)

            response.redirect(request.queryParams("redirect"))
        }
    }
}