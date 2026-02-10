package me.t.technobroadcastbot.model

import kotlinx.serialization.Serializable

@Serializable
data class Published(
    val articles: MutableList<Article> = mutableListOf()
)