package com.adamratzman.geicobot.db

import com.adamratzman.geicobot.chat.Dialog

data class User(
    val id: String,
    val recentlyPlayed: MutableList<RecentlyPlayed> = mutableListOf(),
    val friends: MutableList<String> = mutableListOf(),
    val friendRequestsReceived: MutableList<String> = mutableListOf(),
    val friendRequestsSent: MutableList<String> = mutableListOf(),
    val conversation: MutableList<Dialog> = mutableListOf(),
    var bio: String? = null
)

data class RecentlyPlayed(
    val trackId: String,
    val playlistId: String?,
    val albumId: String?,
    val start: Long
)