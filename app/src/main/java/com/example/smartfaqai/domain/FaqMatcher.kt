package com.example.smartfaqai.domain

import com.example.smartfaqai.data.entity.FaqEntity
import com.example.smartfaqai.util.TextNormalizer
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Scores every FAQ against the user query with a weighted blend of
 * TF cosine similarity, token overlap (Jaccard), and Levenshtein
 * similarity, then returns only the highest-scoring FAQ above the
 * confidence threshold.
 */
object FaqMatcher {
    private const val MIN_SCORE = 0.60
    private const val COSINE_WEIGHT = 0.55
    private const val OVERLAP_WEIGHT = 0.25
    private const val LEVENSHTEIN_WEIGHT = 0.20

    fun findBestMatch(query: String, faqs: List<FaqEntity>): FaqEntity? {
        val queryTokens = TextNormalizer.tokenize(query)
        if (queryTokens.isEmpty() || faqs.isEmpty()) return null

        val queryVector = termFrequency(queryTokens)
        val normalizedQuery = queryTokens.joinToString(" ")

        var best: FaqEntity? = null
        var bestScore = 0.0

        for (faq in faqs) {
            val faqTokens = TextNormalizer.tokenize(faq.question)
            if (faqTokens.isEmpty()) continue

            val score = combinedScore(
                queryTokens = queryTokens,
                queryVector = queryVector,
                normalizedQuery = normalizedQuery,
                faqTokens = faqTokens
            )
            if (score > bestScore) {
                bestScore = score
                best = faq
            }
        }

        return best.takeIf { bestScore >= MIN_SCORE }
    }

    internal fun combinedScore(
        queryTokens: List<String>,
        queryVector: Map<String, Double>,
        normalizedQuery: String,
        faqTokens: List<String>
    ): Double {
        val cosine = cosineSimilarity(queryVector, termFrequency(faqTokens))
        val overlap = tokenOverlap(queryTokens.toSet(), faqTokens.toSet())
        val levenshtein = levenshteinSimilarity(normalizedQuery, faqTokens.joinToString(" "))
        return (cosine * COSINE_WEIGHT) + (overlap * OVERLAP_WEIGHT) + (levenshtein * LEVENSHTEIN_WEIGHT)
    }

    internal fun termFrequency(tokens: List<String>): Map<String, Double> {
        if (tokens.isEmpty()) return emptyMap()
        val counts = tokens.groupingBy { it }.eachCount()
        val totalTerms = tokens.size.toDouble()
        return counts.mapValues { (_, count) -> count / totalTerms }
    }

    internal fun cosineSimilarity(
        first: Map<String, Double>,
        second: Map<String, Double>
    ): Double {
        if (first.isEmpty() || second.isEmpty()) return 0.0

        val dotProduct = first.entries.sumOf { (term, weight) ->
            weight * second.getOrDefault(term, 0.0)
        }
        val firstMagnitude = sqrt(first.values.sumOf { it * it })
        val secondMagnitude = sqrt(second.values.sumOf { it * it })
        if (firstMagnitude == 0.0 || secondMagnitude == 0.0) return 0.0
        return dotProduct / (firstMagnitude * secondMagnitude)
    }

    internal fun tokenOverlap(first: Set<String>, second: Set<String>): Double {
        if (first.isEmpty() || second.isEmpty()) return 0.0
        val intersection = first.intersect(second).size.toDouble()
        val union = first.union(second).size.toDouble()
        return intersection / union
    }

    internal fun levenshteinSimilarity(first: String, second: String): Double {
        if (first == second) return 1.0
        if (first.isEmpty() || second.isEmpty()) return 0.0
        val distance = levenshtein(first, second).toDouble()
        return 1.0 - (distance / max(first.length, second.length).toDouble())
    }

    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[a.length][b.length]
    }
}
