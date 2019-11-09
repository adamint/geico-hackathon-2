package com.adamratzman.geicobot.commands

import com.adamratzman.geicobot.system.AutowiredCommand
import com.adamratzman.geicobot.system.Category
import com.adamratzman.geicobot.system.Command
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse
import spark.Session
import kotlin.random.Random

@AutowiredCommand
class Coinflip : Command(Category.FUN, "coinflip", "take a chance and flip a coin!", "lex.coinflip") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        consumer("Flipped a coin. Result: <b>${if (Random.nextBoolean()) "Heads" else "Tails"}</b>")
    }
}
