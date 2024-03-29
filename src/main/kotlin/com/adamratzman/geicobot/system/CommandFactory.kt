package com.adamratzman.geicobot.system

import com.adamratzman.geicobot.chat.Dialog
import com.adamratzman.geicobot.chat.SenderType
import com.adamratzman.geicobot.chat.postToLex
import com.adamratzman.geicobot.commands.TextCommand
import com.adamratzman.geicobot.db.update
import com.adamratzman.geicobot.spotify.getUser
import org.reflections.Reflections
import spark.Session
import java.lang.reflect.Modifier
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val dialogForUsers = mutableMapOf<String, MutableList<Dialog>>()

class CommandFactory {
    val commands: MutableList<Command> = mutableListOf()
    val executor: ExecutorService = Executors.newCachedThreadPool()

    init {
        commands.addAll(
            Reflections("com.adamratzman.geicobot.commands")
                .getTypesAnnotatedWith(AutowiredCommand::class.java)
                .filter { !Modifier.isAbstract(it.modifiers) }
                .map { it.newInstance() as Command }
        )
    }

    fun onMessageEvent(input: String, session: Session, consumer: (String?) -> Unit) {
        if (input.isEmpty()) return

        val user = session.getUser()
        user.conversation.add(Dialog(input, SenderType.USER))
        update("users", user.id, user)

        val lexResponse = postToLex(input, session.id())

        dialogForUsers.putIfAbsent(session.id(), mutableListOf())
        dialogForUsers[session.id()]!!.add(Dialog(input, SenderType.USER))

        val foundCommand = commands.find { command ->
            input.startsWith(command.name, true) ||
                    ( lexResponse.message().startsWith(command.trigger)
                    && commands.none { input.startsWith(it.name, true) })
        } ?: commands.first { it is TextCommand }

        foundCommand.executeBase(input, lexResponse, session) { commandResponse ->
            commandResponse?.let {
                val newUser = session.getUser()
                newUser.conversation.add(Dialog(commandResponse, SenderType.BOT))
                update("users", newUser.id, newUser)
            }
            consumer(commandResponse)
        }
    }
}