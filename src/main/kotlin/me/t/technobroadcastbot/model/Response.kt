package me.t.technobroadcastbot.model

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val articles: List<Article>
)