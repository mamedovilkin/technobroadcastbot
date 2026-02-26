package me.t.technobroadcastbot

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import me.t.technobroadcastbot.model.Article
import me.t.technobroadcastbot.model.Published
import me.t.technobroadcastbot.model.Response
import me.t.technobroadcastbot.util.escapeMarkdownV2
import me.t.technobroadcastbot.util.filterArticle
import java.io.File

private val NEWS_API_KEY = System.getenv("NEWS_API_KEY")
private val BOT_TOKEN = System.getenv("BOT_TOKEN")
private val CHANNEL_ID = System.getenv("CHANNEL_ID")
private const val CACHE_FILE = "published.json"

fun main() {
    runBlocking {
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                connectTimeoutMillis = 60_000
                requestTimeoutMillis = 60_000
                socketTimeoutMillis = 60_000
            }
        }

        val article = fetchArticle(client)

        if (article != null) {
            postToChannel(client, article)
        }

        client.close()
    }
}

suspend fun fetchArticle(client: HttpClient): Article? {
    val json = Json { ignoreUnknownKeys = true }

    val response = client.get("https://gnews.io/api/v4/top-headlines") {
        header("User-Agent", "Mozilla/5.0")
        parameter("token", NEWS_API_KEY)
        parameter("lang", "ru")
        parameter("country", "ru")
        parameter("topic", "technology")
    }

    val news = json.decodeFromString(Response.serializer(), response.body())
    val published = loadPublished()
    val words = listOf("беспилотник", "бпла", "дрон")

    news.articles.forEach { article ->
        if (!article.title.filterArticle(words) && !article.description.filterArticle(words) && published.articles[article.url] == null) {
            published.articles[article.url] = article
            savePublished(published)

            return article
        }
    }

    return null
}

fun loadPublished(): Published {
    val json = Json { ignoreUnknownKeys = true }

    val file = File(CACHE_FILE)
    if (!file.exists()) {
        return Published()
    }

    return json.decodeFromString(
        Published.serializer(),
        file.readText()
    )
}

fun savePublished(published: Published) {
    val json = Json { prettyPrint = true }

    File(CACHE_FILE).writeText(
        json.encodeToString(Published.serializer(), published)
    )
}

suspend fun postToChannel(client: HttpClient, article: Article) {
    val title = article.title.escapeMarkdownV2()
    val description = article.description.escapeMarkdownV2()
    val url = article.url
    val text = "*$title*\n\n$description\n\n[Читать далее]($url)"

    if (article.image != null) {
        client.post("https://api.telegram.org/bot$BOT_TOKEN/sendPhoto") {
            header("User-Agent", "Mozilla/5.0")
            parameter("chat_id", CHANNEL_ID)
            parameter("photo", article.image)
            parameter("caption", text)
            parameter("parse_mode", "MarkdownV2")
        }
    } else {
        client.post("https://api.telegram.org/bot$BOT_TOKEN/sendMessage") {
            header("User-Agent", "Mozilla/5.0")
            parameter("chat_id", CHANNEL_ID)
            parameter("text", text)
            parameter("parse_mode", "MarkdownV2")
        }
    }
}