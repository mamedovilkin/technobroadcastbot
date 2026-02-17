package me.t.technobroadcastbot.model

import kotlinx.serialization.Serializable

@Serializable
data class Published(
    val articles: MutableMap<String, Article> = mutableMapOf()
)