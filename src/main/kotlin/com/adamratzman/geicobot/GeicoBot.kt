package com.adamratzman.geicobot

import com.adamratzman.geicobot.chat.getNewScraper
import com.adamratzman.geicobot.chat.scraper
import com.adamratzman.geicobot.db.databaseSetup
import com.adamratzman.geicobot.http.*
import com.adamratzman.geicobot.system.CommandFactory
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Options
import com.google.gson.Gson
import com.rethinkdb.RethinkDB
import spark.Request
import spark.Spark
import spark.Spark.port
import spark.staticfiles.StaticFilesConfiguration
import spark.template.handlebars.HandlebarsTemplateEngine
import java.util.concurrent.Executors

val handlebars = HandlebarsTemplateEngine()
val executor = Executors.newScheduledThreadPool(0)
val gson = Gson()
val r = RethinkDB.r
val connection = r.connection().hostname("localhost").connect()

val commandFactory = CommandFactory()

val botName = "GEICObot"

fun main(args: Array<String>) {
    System.setProperty("webdriver.chrome.driver", args[0])

    port(getHerokuAssignedPort())
    val staticFileHandler = StaticFilesConfiguration()
    staticFileHandler.configure("/public")

    Spark.before("/*") { request, response ->
        staticFileHandler.consume(request.raw(), response.raw())
    }

    Spark.exception(Exception::class.java) { exception, _, _ ->
        exception.printStackTrace()
    }

    Spark.notFound { request, _ ->
        val map = getMap(request, "404 - Not Found", "404", false)
        map["color"] = getRandomColor()
        handlebars.render(map, "404.hbs")
    }
    registerHelpers()

    GeicoBot()
}

class GeicoBot {
    init {
        home()
        bot()
        databaseSetup()
        spotifyCallback()
        profile()

        scraper = getNewScraper()
    }

}

private fun registerHelpers() {
    val field = handlebars::class.java.getDeclaredField("handlebars")
    field.isAccessible = true
    val handle = field.get(handlebars) as Handlebars

    handle.registerHelper("ifeq") { first: Any?, options: Options ->
        if (options.params[0].toString().equals(first?.toString(), true)) {
            options.fn()
        } else options.inverse()
    }
}

internal fun getMap(
    request: Request,
    pageTitle: String,
    pageId: String,
    positionBottom: Boolean
): MutableMap<String, Any?> {
    val map = mutableMapOf<String, Any?>()
    map["title"] = "$botName | $pageTitle"
    map["page"] = pageId
    map["position-bottom"] = positionBottom
    map["color"] = getRandomColor()
    map["spotify"] = request.session().attribute("spotify")
    map["botName"] = botName

    map["profile"] = request.session().attribute<Any?>("spotify") != null

    // meta
    map["description"] = botName

    return map
}

fun getHerokuAssignedPort(): Int {
    val processBuilder = ProcessBuilder()
    return if (processBuilder.environment()["PORT"] != null) {
        Integer.parseInt(processBuilder.environment()["PORT"])
    } else 80
}