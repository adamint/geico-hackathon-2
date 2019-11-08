package com.adamratzman.geicobot.chat

import com.adamratzman.geicobot.awsPrivate
import com.adamratzman.geicobot.awsPublic
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.services.lexruntime.LexRuntimeClient
import software.amazon.awssdk.services.lexruntime.model.PostTextRequest
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse

val lex = LexRuntimeClient.builder()
    .credentialsProvider { AwsBasicCredentials.create(awsPublic, awsPrivate) }
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