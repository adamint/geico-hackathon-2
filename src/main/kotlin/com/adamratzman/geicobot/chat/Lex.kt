package com.adamratzman.geicobot.chat

import software.amazon.awssdk.services.lexruntime.LexRuntimeClient
import software.amazon.awssdk.services.lexruntime.model.PostTextRequest
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse

val lex = LexRuntimeClient.builder()
    .build()

fun postToLex(input: String, sessionId: String): PostTextResponse {
    val request = PostTextRequest
        .builder()
        .botName("GEICObot")
        .botAlias("\$LATEST")
        .userId(sessionId)
        .inputText(input)
        .build()

    return lex.postText(request)
}