package com.adamratzman.geicobot.system

import com.adamratzman.geicobot.botName
import com.adamratzman.geicobot.commandFactory
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse
import spark.Session

annotation class AutowiredCommand

abstract class Command(
    val category: Category,
    val name: String,
    val description: String,
    val trigger: String
) {
    abstract fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit)
}

enum class Category(val fancyName: String, val description: String) {
    MUSIC("Music", "Play your favorite tracks or listen to the radio, all inside Discord"),
    FUN("Fun", "Bored? Not interested in the games? We have a lot of commands for you to check out here!"),
    BOT_INFO("Information", "Information about $botName"),
    CHAT("Chat", "Commands relating to chatting with $botName"),
    PROFILE("Profile", "Get/set profile information")
    ;

    override fun toString(): String {
        return fancyName
    }

    fun getCommands(): List<Command> {
        return commandFactory.commands.filter { it.category == this }
    }
}

fun <T> List<T>.removeFirstItems(num: Int): MutableList<T> {
    val list = toMutableList()
    repeat(num) {
        if (list.isNotEmpty()) list.removeAt(0)
    }

    return list
}

fun String.splitSpaces(): List<String> {
    return split(" ").filterNot { it.isEmpty() }
}

fun List<String>.concat() = joinToString(" ")