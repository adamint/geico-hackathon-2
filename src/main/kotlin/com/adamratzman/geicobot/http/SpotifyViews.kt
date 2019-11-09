package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.GeicoBot
import com.adamratzman.geicobot.getMap
import com.adamratzman.geicobot.handlebars
import com.adamratzman.geicobot.spotify.assureLoggedIn
import com.adamratzman.geicobot.spotify.fancyString
import com.adamratzman.geicobot.spotify.getSpotifyApi
import spark.Spark.get

fun GeicoBot.spotifyViews() {
    get("/track/:id") { request, response ->
        if (!assureLoggedIn(request, response)) return@get ""
        val track = request.session().getSpotifyApi().tracks.getTrack(request.params(":id")).complete()!!
        val map = getMap(request, track.fancyString(), "track", true)
        map["track"] = track
        map["artwork"] = track.album.images.firstOrNull()?.url
            ?: "https://cdn4.iconfinder.com/data/icons/lyrics/154/dics-cd-music-audio-track-512.png"
        map["authorsString"] = "By ${track.artists.joinToString(", ") { it.name }}"

        handlebars.render(map, "track-big.hbs")
    }

    get("/album/:id") { request, response ->
        if (!assureLoggedIn(request, response)) return@get ""
        val album = request.session().getSpotifyApi().albums.getAlbum(request.params(":id")).complete()!!
        val map = getMap(request, album.fancyString(), "album", true)
        map["album"] = album
        map["artwork"] = album.images.firstOrNull()?.url
            ?: "https://cdn4.iconfinder.com/data/icons/lyrics/154/dics-cd-music-audio-track-512.png"
        map["authorsString"] = "By ${album.artists.joinToString(", ") { it.name }}"

        handlebars.render(map, "album-big.hbs")
    }

    get("/playlist/:id") { request, response ->
        if (!assureLoggedIn(request, response)) return@get ""
        val playlist = request.session().getSpotifyApi().playlists.getPlaylist(request.params(":id"), market = null).complete()!!
        val map = getMap(request, playlist.fancyString(), "playlist", true)
        map["playlist"] = playlist
        map["artwork"] = playlist.images.firstOrNull()?.url
            ?: "https://cdn4.iconfinder.com/data/icons/lyrics/154/dics-cd-music-audio-track-512.png"
        map["authorsString"] = "(${playlist.tracks.total} tracks) by ${playlist.owner.displayName?.let { "${playlist.owner.displayName} (${playlist.owner.id})" } ?: playlist.owner.id}"

        handlebars.render(map, "playlist-big.hbs")
    }

    get("/artist/:id") { request, response ->
        if (!assureLoggedIn(request, response)) return@get ""
        val artist = request.session().getSpotifyApi().artists.getArtist(request.params(":id")).complete()!!
        val map = getMap(request, artist.name, "playlist", true)
        map["artist"] = artist
        map["artwork"] = artist.images.firstOrNull()?.url
            ?: "https://cdn4.iconfinder.com/data/icons/lyrics/154/dics-cd-music-audio-track-512.png"

        handlebars.render(map, "artist-big.hbs")
    }
}