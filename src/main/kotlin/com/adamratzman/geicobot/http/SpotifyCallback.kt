package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.GeicoBot
import com.adamratzman.geicobot.connection
import com.adamratzman.geicobot.db.User
import com.adamratzman.geicobot.db.getUser
import com.adamratzman.geicobot.gson
import com.adamratzman.geicobot.r
import com.adamratzman.geicobot.spotify.sId
import com.adamratzman.geicobot.spotify.sPassword
import com.adamratzman.spotify.spotifyClientApi
import spark.Spark.get

fun GeicoBot.spotifyCallback() {
    get("/spotify-callback") { request, response ->
        try {
            val code = request.queryParams("code")
            val clientApi = spotifyClientApi {
                credentials {
                    clientId = sId
                    clientSecret = sPassword
                    redirectUri = "http://localhost/spotify-callback"
                }
                authorization {
                    authorizationCode = code
                }
            }.build()

            request.session().attribute("spotify", clientApi)

            val userId = clientApi.userId
            if (getUser(userId) == null) r.db("geico").table("users")
                .insert(r.json(gson.toJson(User(userId)))).run<Any>(connection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        response.redirect("/?login=true")
    }
}