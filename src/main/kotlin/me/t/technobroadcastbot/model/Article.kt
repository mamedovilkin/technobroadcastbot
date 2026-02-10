package me.t.technobroadcastbot.model

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val title: String,
    val description: String,
    val image: String?,
    val url: String
)