package com.adamratzman.geicobot.commands

import com.adamratzman.geicobot.system.AutowiredCommand
import com.adamratzman.geicobot.system.Category
import com.adamratzman.geicobot.system.Command
import org.jsoup.Jsoup
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse
import spark.Session

@AutowiredCommand
class UnixFortune : Command(Category.FUN, "fortune", "in the mood for a unix fortune? us too", "lex.fortune") {
    override fun executeBase(input: String, response: PostTextResponse, session: Session, consumer: (String?) -> Unit) {
        consumer(
            Jsoup.connect("http://motd.ambians.com/quotes.php/name/linux_fortunes_random/toc_id/1-1-1")
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get().getElementsByTag("pre")[0].text()
        )
    }
}
