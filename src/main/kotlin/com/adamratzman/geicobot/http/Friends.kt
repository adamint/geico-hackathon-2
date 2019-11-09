package com.adamratzman.geicobot.http

import com.adamratzman.geicobot.GeicoBot
import com.adamratzman.geicobot.db.getUser
import com.adamratzman.geicobot.db.update
import com.adamratzman.geicobot.getMap
import com.adamratzman.geicobot.handlebars
import com.adamratzman.geicobot.spotify.assureLoggedIn
import com.adamratzman.geicobot.spotify.fancyString
import com.adamratzman.geicobot.spotify.getUser
import com.adamratzman.geicobot.spotify.spotifyApi
import com.github.marlonlom.utilities.timeago.TimeAgo
import spark.Spark.get
import spark.Spark.path

fun GeicoBot.friends() {
    path("/friends") {
        get("") { request, response ->
            if (!assureLoggedIn(request, response)) return@get ""
            val user = request.session().getUser()
            val friends = user.friends.map { getUser(it, null) }
            val map = getMap(request, "Friends", "friends", user.friends.size < 3)

            map["hasFriends"] = friends.isNotEmpty()
            map["friends"] = friends
            map["hasFriendRequests"] = user.friendRequestsReceived.isNotEmpty()
            map["friendRequests"] = user.friendRequestsReceived.map { getUser(it, null) }

            val friendFeed = friends.map { friend ->
                friend.recommendations.map { recommendation ->
                    friend to Triple(spotifyApi.tracks.getTrack(recommendation.first).complete(), TimeAgo.using(recommendation.second),recommendation.second)
                }
            }.flatten().sortedByDescending { it.second.third }.map { it.first to Triple(it.second.first, it.second.second, it.second.first?.fancyString()) }

            map["hasFriendFeed"] = friendFeed.isNotEmpty()
            map["friendFeed"] = friendFeed
            if (friendFeed.isNotEmpty()) map["position-bottom"] = false

            handlebars.render(map, "friends.hbs")
        }

        get("/respond/:otherId") { request, response ->
            val accept = request.queryParams("accept")?.toBoolean() ?: false

            val otherUser = getUser(request.params(":otherId"), null)
            val user = request.session().getUser()
            user.friendRequestsReceived.remove(otherUser.id)
            if (accept) {
                user.friends.add(otherUser.id)
                otherUser.friends.add(user.id)
                update("users", otherUser.id, otherUser)
            }

            update("users", user.id, user)
            response.redirect("/friends")

        }

        get("/request/:otherId") { request, response ->
            val otherUser = getUser(request.params(":otherId"), null)
            val user = request.session().getUser()
            otherUser.friendRequestsReceived.add(user.id)
            update("users", otherUser.id, otherUser)

            response.redirect("/profiles")
        }
    }
}