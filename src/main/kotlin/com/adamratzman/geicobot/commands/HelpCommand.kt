package com.adamratzman.geicobot.commands

import com.adamratzman.geicobot.commandFactory
import com.adamratzman.geicobot.system.AutowiredCommand
import com.adamratzman.geicobot.system.Category
import com.adamratzman.geicobot.system.Command
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse
import spark.Session
import java.util.stream.Collectors

@AutowiredCommand
class Help : Command(Category.BOT_INFO, "help", "see a list of commands you can use", "lex.help") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val arguments = input.split(" ").toMutableList().apply { removeAt(0) }
        var text = "";
        if (arguments.size > 0) {
            val name = arguments.joinToString(" ")
            commandFactory.commands.forEach {
                if (it.name.equals(name, true)) {
                    text += """<b>${it.name}</b> command: ${it.description}"""
                    consumer(text)
                }
            }
        }
        if (text.isEmpty()) {
            if (!input.equals("help", true)) text += "No command was found with name $name - showing default help menu instead\n"
            Category.values().forEach { category ->
                text += "<i>${category.fancyName}</i> : " + category.getCommands().toMutableList().shuffled()
                    .map { "`" + it.name + "`" }.stream().collect(Collectors.joining("    ")) +
                        "\n"
            }

            text += "<i>To see detailed information about a command, type *help command name</i>"

            consumer(text)
        }
    }
}