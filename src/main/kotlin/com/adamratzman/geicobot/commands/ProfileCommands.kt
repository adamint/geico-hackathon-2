package com.adamratzman.geicobot.commands

import com.adamratzman.geicobot.db.update
import com.adamratzman.geicobot.spotify.getSpotifyApi
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

@AutowiredCommand
class AddFavorite : Command(Category.MUSIC, "favorite", "add a track to your favorites", "lex.addfavorite") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val split = input.splitSpaces().removeFirstItems(1)
        if (split.isEmpty()) consumer("Invalid song!")
        else {
            val user = session.getUser()
            val track = session.getSpotifyApi().search.searchTrack(split.concat()).complete().firstOrNull()
            if (track == null) consumer("I couldn't find that track!")
            else {
                user.favoriteTracks.add(track.id to System.currentTimeMillis())
                user.favoriteTracks.removeIf { pair -> user.favoriteTracks.count { it.first == pair.first } > 1 }
                update("users", user.id, user)
                consumer("Added <u>${track.name}</u> by ${track.artists.joinToString(", ") {it.name}} to your <a target='_blank' href='/favorites'>favorites</a>")
            }
        }
    }
}


