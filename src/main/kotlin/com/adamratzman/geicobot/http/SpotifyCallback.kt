package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.GeicoBot
import com.adamratzman.geicobot.db.getUser
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
            request.session().attribute("userId", clientApi.userId)

            request.session().attribute("user", getUser(clientApi.userId))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        response.redirect("/?login=true")
    }
}