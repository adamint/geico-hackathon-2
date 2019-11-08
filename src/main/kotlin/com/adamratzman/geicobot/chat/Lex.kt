package com.adamratzman.geicobot.chat

import com.adamratzman.geicobot.awsPrivate
import com.adamratzman.geicobot.awsPublic
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.services.lexruntime.LexRuntimeClient
import software.amazon.awssdk.services.lexruntime.model.PostTextRequest
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse

lateinit var lex: LexRuntimeClient
fun postToLex(input: String, sessionId: String): PostTextResponse {
    try{
        lex =  LexRuntimeClient.builder()
            .credentialsProvider { AwsBasicCredentials.create(awsPublic, awsPrivate) }
            .build()
            .apply { println(this) }

        println("fajkejkrwe")

        println("hiiiii")
    val request = PostTextRequest
        .builder()
        .botName("GEICObot")
        .botAlias("\$LATEST")
        .userId(sessionId)
        .inputText(input)
        .build()

    return lex.postText(request)}
    catch (e:Exception){e.printStackTrace();throw e}
}