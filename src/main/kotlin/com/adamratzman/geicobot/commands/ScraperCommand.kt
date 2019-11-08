package com.adamratzman.geicobot.commands

import com.adamratzman.geicobot.chat.respond
import com.adamratzman.geicobot.chat.scraper
import com.adamratzman.geicobot.system.AutowiredCommand
import com.adamratzman.geicobot.system.Category
import com.adamratzman.geicobot.system.Command
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse
import spark.Session

@AutowiredCommand
class ScraperCommand : Command(Category.CHAT, "scraper", "cleverbot scraper", "lex.notfound") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        try {
            respond(scraper, input) { cleverBotResponse ->
                consumer(cleverBotResponse)
            }
        } catch (e: Exception) {
            consumer(null)
        }
    }
}