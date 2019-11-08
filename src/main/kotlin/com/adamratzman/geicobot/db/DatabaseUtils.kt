package com.adamratzman.geicobot.db

import com.adamratzman.geicobot.connection
import com.adamratzman.geicobot.gson
import com.adamratzman.geicobot.r

fun databaseSetup() {
    if (!r.dbList().run<List<String>>(connection).contains("geico")) {
        println("creating database..")
        r.dbCreate("geico").run<Any>(connection)
    }

    if (!r.db("geico").tableList().run<List<String>>(connection).contains("users")) {
        println("creating user table..")
        r.db("geico").tableCreate("users").run<Any>(connection)
    }
}

fun getUser(id: String): User? {
    return asPojo(gson, r.db("geico").table("users").get(id).run(connection), User::class.java)
}