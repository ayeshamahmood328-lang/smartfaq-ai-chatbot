package com.example.smartfaqai.util

import java.util.Locale

object TextNormalizer {
    private val stopWords = setOf(
        "a", "an", "and", "are", "as", "at", "be", "by", "can", "could",
        "did", "do", "does", "for", "from", "had", "has", "have", "how",
        "i", "in", "is", "it", "me", "my", "of", "on", "or", "our",
        "should", "that", "the", "their", "this", "to", "was", "we", "were",
        "what", "when", "where", "which", "who", "why", "will", "with",
        "would", "you", "your"
    )

    fun tokenize(input: String): List<String> =
        input.lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .split(" ")
            .filter { token -> token.isNotBlank() && token !in stopWords }

    fun normalize(input: String): String = tokenize(input).joinToString(" ")
}
