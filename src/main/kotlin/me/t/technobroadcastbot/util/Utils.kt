package me.t.technobroadcastbot.util

fun String.escapeMarkdownV2(): String {
    return this.replace("\\", "\\\\")
        .replace("_", "\\_")
        .replace("*", "\\*")
        .replace("[", "\\[")
        .replace("]", "\\]")
        .replace("(", "\\(")
        .replace(")", "\\)")
        .replace("~", "\\~")
        .replace("`", "\\`")
        .replace(">", "\\>")
        .replace("#", "\\#")
        .replace("+", "\\+")
        .replace("-", "\\-")
        .replace("=", "\\=")
        .replace("|", "\\|")
        .replace("{", "\\{")
        .replace("}", "\\}")
        .replace(".", "\\.")
        .replace("!", "\\!")
}

fun String.filterArticle(words: List<String>): Boolean {
    this.split(" ").forEach { word ->
        words.forEach {
            if (word.startsWith(it, true)) return true
        }
    }

    return false
}