package com.adamratzman.geicobot.chat

data class Dialog(val text: String, val sender: SenderType, val time: Long = System.currentTimeMillis())
enum class SenderType {
    BOT, USER
}