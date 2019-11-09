package com.adamratzman.geicobot.db

import com.adamratzman.geicobot.chat.Dialog
import com.adamratzman.spotify.models.Track
import com.github.marlonlom.utilities.timeago.TimeAgo

data class User(
    val id: String,
    val profileImage: String,
    var topTracks: List<Track>,
    var nickname: String? = null,
    val recentlyPlayed: MutableList<RecentlyPlayed> = mutableListOf(),
    val friends: MutableList<String> = mutableListOf(),
    val friendRequestsReceived: MutableList<String> = mutableListOf(),
    val friendRequestsSent: MutableList<String> = mutableListOf(),
    val conversation: MutableList<Dialog> = mutableListOf(),
    var bio: String? = null
) {
    var lastActive: Long = System.currentTimeMillis()
    var favoriteTracks: MutableList<Pair<String, Long>> = mutableListOf()

    val lastActiveString get() = TimeAgo.using(lastActive)
}

data class RecentlyPlayed(
    val trackId: String,
    val playlistId: String?,
    val albumId: String?,
    val start: Long
)