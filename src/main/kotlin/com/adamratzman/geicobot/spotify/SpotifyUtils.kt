package com.adamratzman.geicobot.spotify

import com.adamratzman.geicobot.db.User
import com.adamratzman.geicobot.db.getUser
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.spotifyAppApi
import com.adamratzman.spotify.spotifyClientApi
import spark.Request
import spark.Response
import spark.Session

val sId = "9e0323b3b595424fae96001d752f9638"
val sPassword = "1f8911e216a4476ca973c5a622fc15bd"

val spotifyApi = spotifyAppApi {
    credentials {
        clientId = sId
        clientSecret = sPassword
    }
}.buildPublic()

val authorizationUrl = spotifyClientApi {
    credentials {
        clientId = sId
        clientSecret = sPassword
        redirectUri = "http://localhost/spotify-callback"
    }
}.getAuthorizationUrl(
    SpotifyScope.PLAYLIST_READ_PRIVATE,
    SpotifyScope.USER_LIBRARY_READ,
    SpotifyScope.USER_READ_PRIVATE, SpotifyScope.USER_TOP_READ,
    SpotifyScope.USER_FOLLOW_READ,
    SpotifyScope.STREAMING,
    SpotifyScope.USER_MODIFY_PLAYBACK_STATE,
    SpotifyScope.USER_READ_PLAYBACK_STATE,
    SpotifyScope.USER_READ_CURRENTLY_PLAYING,
    SpotifyScope.USER_READ_RECENTLY_PLAYED
)

fun isLoggedIn(request: Request): Boolean = request.session().attribute<Any?>("spotify") != null

fun assureLoggedIn(request: Request, response: Response): Boolean {
    return if (!isLoggedIn(request)) {
        response.redirect(authorizationUrl)
        false
    } else true
}

fun Session.getUser():User = getUser(attribute<String>("userId"))