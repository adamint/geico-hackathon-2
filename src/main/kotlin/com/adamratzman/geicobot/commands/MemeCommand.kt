package com.adamratzman.geicobot.commands

import com.adamratzman.geicobot.system.AutowiredCommand
import com.adamratzman.geicobot.system.Category
import com.adamratzman.geicobot.system.Command
import org.json.JSONObject
import org.jsoup.Jsoup
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse
import spark.Session

@AutowiredCommand
class Meme : Command(Category.FUN, "meme", "get a random meme from giphy", "lex.meme") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        val imageUrl = JSONObject(
            Jsoup.connect("https://api.giphy.com/v1/gifs/random").data(
                "api_key",
                "3IXi2saqRfIt5h1GrPswmkbzAX6s33qP"
            )
                .ignoreContentType(true).get().body().text()
        ).getJSONObject("data").getString("image_url")
        consumer("<img src='$imageUrl'>")
    }
}
