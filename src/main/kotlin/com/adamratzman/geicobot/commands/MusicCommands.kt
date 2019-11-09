package com.adamratzman.geicobot.commands

import com.adamratzman.geicobot.system.AutowiredCommand
import com.adamratzman.geicobot.system.Command
/*
@AutowiredCommand
class Play : Command(Category.MUSIC, "play", "play songs, playlists, or your personal music library!", "p") {
    override fun executeBase(arguments: MutableList<String>, event: MessageReceivedEvent) {
        if (arguments.size == 0) {
            event.channel.send("You can search or play single tracks from Youtube or Spotify by typing *play [search or url]*.")
        } else {
            arguments.concat().load(event.member!!, event.textChannel)
        }
    }
}*/