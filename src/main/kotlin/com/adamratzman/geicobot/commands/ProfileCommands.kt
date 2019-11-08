package com.adamratzman.geicobot.commands

import com.adamratzman.geicobot.db.update
import com.adamratzman.geicobot.spotify.getUser
import com.adamratzman.geicobot.system.*
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse
import spark.Session

@AutowiredCommand
class GetNickname : Command(Category.PROFILE, "what's my nickname", "get your current nickname", "lex.getnickname") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val nickname = session.getUser().nickname
        nickname?.let { consumer("Your nickname is <b>$nickname</b>") } ?: consumer("You don't have a set nickname!")
    }
}

@AutowiredCommand
class SetNickname : Command(Category.PROFILE, "set my nickname to", "set your nickname", "lex.setnickname") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val split = input.splitSpaces().removeFirstItems(4)
        if (split.isEmpty()) consumer("You need to put in a nickname!")
        else {
            val user = session.getUser()
            user.nickname = split.concat()
            update("users", user.id, user)
            consumer("Updated your nickname. You're now <b>${user.nickname}</b>")
        }
    }
}

@AutowiredCommand
class GetBio : Command(Category.PROFILE, "what's my bio", "get your current bio", "lex.getbio") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val nickname = session.getUser().bio
        nickname?.let { consumer("Your bio is <b>$nickname</b>") } ?: consumer("You don't have a set bio!")
    }
}

@AutowiredCommand
class SetBio : Command(Category.PROFILE, "set my bio to", "set your bio", "lex.setbio") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val split = input.splitSpaces().removeFirstItems(4)
        if (split.isEmpty()) consumer("You need to put in a bio!")
        else {
            val user = session.getUser()
            user.bio = split.concat()
            update("users", user.id, user)
            consumer("Updated your bio. Your bio is now <b>${user.bio}</b>")
        }
    }
}