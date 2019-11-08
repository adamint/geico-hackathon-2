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

fun getUser(id: String): User {
    var user = asPojo(gson, r.db("geico").table("users").get(id).run(connection), User::class.java)
    if (user == null) {
        user = User(id)
        insert("users", user)
    }

    return user
}

fun update(table: String, id: String, any: Any) {
    r.db("geico").table(table).get(id).update(r.json(gson.toJson(any))).run<Any>(connection)
}

fun insert(table: String, any: Any) {
    r.db("geico").table(table).insert(r.json(gson.toJson(any))).run<Any>(connection)
}