package com.adamratzman.geicobot.chat

import com.adamratzman.geicobot.cleverbotKey
import com.michaelwflaherty.cleverbotapi.CleverBotQuery

fun respond(input: String, consumer: (String) -> Unit) {
    val bot = CleverBotQuery(cleverbotKey, input)
    bot.sendRequest()

    consumer(bot.response)
}

data class CleverbotResponse(val status: Int, val response: String, val outputType: String? = null)