package com.adamratzman.geicobot.chat

import com.adamratzman.geicobot.http.doesThrow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.chrome.ChromeDriver

lateinit var scraper:ChromeDriver

fun getNewScraper(): ChromeDriver = runBlocking {
    val driver = ChromeDriver()
    driver.get("https://cleverbot.com")
    delay(1500)

    driver.findElementsByClassName("service-icon")[4].click()

    driver
}

fun respond(driver: ChromeDriver, input: String, consumer: (String) -> Unit) = runBlocking {
    driver.findElementByClassName("stimulus").sendKeys(input)
    driver.findElementById("avatarform").submit()

    launch {
        while (doesThrow<NoSuchElementException> { driver.findElementById("snipTextIcon") } || driver.findElementById("snipTextIcon").getCssValue(
                "opacity"
            ) != "1") {
            delay(50)
        }
        val text = driver.findElementById("line1").findElement(By.className("bot")).text
        consumer(text)
    }
}

data class CleverbotResponse(val status: Int, val response: String, val outputType: String? = null)