package com.adamratzman.geicobot.chat

data class Dialog(val text: String, val sender: SenderType)
enum class SenderType {
    BOT, USER
}