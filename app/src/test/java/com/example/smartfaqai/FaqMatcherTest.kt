package com.example.smartfaqai

import com.example.smartfaqai.data.entity.FaqEntity
import com.example.smartfaqai.domain.FaqMatcher
import com.example.smartfaqai.util.TextNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class FaqMatcherTest {
    @Test
    fun normalize_lowercases_stripsPunctuation_andRemovesStopWords() {
        assertEquals(
            "cloud computing",
            TextNormalizer.normalize("  What IS Cloud Computing?!  ")
        )
    }

    @Test
    fun tokenize_returnsMeaningfulBagOfWordsTerms() {
        assertEquals(
            listOf("protect", "bank", "account", "online"),
            TextNormalizer.tokenize("How can I protect my bank account online?")
        )
    }

    @Test
    fun termFrequency_buildsNormalizedTfVector() {
        val vector = FaqMatcher.termFrequency(listOf("cloud", "cloud", "storage"))
        assertEquals(2.0 / 3.0, vector.getValue("cloud"), 0.0001)
        assertEquals(1.0 / 3.0, vector.getValue("storage"), 0.0001)
    }

    @Test
    fun cosineSimilarity_returnsOneForSameVector_andZeroForUnrelatedVectors() {
        val cloud = FaqMatcher.termFrequency(listOf("cloud", "computing"))
        val same = FaqMatcher.termFrequency(listOf("cloud", "computing"))
        val health = FaqMatcher.termFrequency(listOf("water", "sleep"))

        assertEquals(1.0, FaqMatcher.cosineSimilarity(cloud, same), 0.0001)
        assertEquals(0.0, FaqMatcher.cosineSimilarity(cloud, health), 0.0001)
    }

    @Test
    fun findBestMatch_returnsClosestFaq() {
        val faqs = listOf(
            FaqEntity(
                id = 1,
                question = "What is cloud computing?",
                normalizedQuestion = TextNormalizer.normalize("What is cloud computing?"),
                answer = "Cloud answer",
                category = "Technology"
            ),
            FaqEntity(
                id = 2,
                question = "How much water should I drink?",
                normalizedQuestion = TextNormalizer.normalize("How much water should I drink?"),
                answer = "Water answer",
                category = "Health"
            )
        )

        val match = FaqMatcher.findBestMatch("what is cloud computing", faqs)
        assertNotNull(match)
        assertEquals(1L, match!!.id)
    }

    @Test
    fun findBestMatch_distinguishesQuestionsWithinTheSameTopic() {
        val faqs = listOf(
            faq(1, "What is cloud computing?"),
            faq(2, "How does cloud computing work?"),
            faq(3, "What are the advantages of cloud computing?"),
            faq(4, "What is cloud storage?"),
            faq(5, "Is cloud computing secure?")
        )

        assertEquals(1L, FaqMatcher.findBestMatch("What is cloud computing?", faqs)?.id)
        assertEquals(2L, FaqMatcher.findBestMatch("How does cloud computing work?", faqs)?.id)
        assertEquals(3L, FaqMatcher.findBestMatch("What are the advantages of cloud computing?", faqs)?.id)
        assertEquals(4L, FaqMatcher.findBestMatch("What is cloud storage?", faqs)?.id)
        assertEquals(5L, FaqMatcher.findBestMatch("Is cloud computing secure?", faqs)?.id)
    }

    @Test
    fun findBestMatch_toleratesRephrasedQuery() {
        val faqs = listOf(
            faq(1, "How do I create a strong password?"),
            faq(2, "How much sleep do adults need?"),
            faq(3, "What is a savings account?")
        )

        val match = FaqMatcher.findBestMatch("how to create a strong password", faqs)
        assertEquals(1L, match?.id)
    }

    @Test
    fun findBestMatch_rejectsWeaklyRelatedQueryBelowThreshold() {
        val faqs = listOf(faq(1, "What is cloud computing?"))
        // Shares only one meaningful token ("computing") with the FAQ.
        assertNull(FaqMatcher.findBestMatch("history of quantum computing hardware research", faqs))
    }

    @Test
    fun findBestMatch_returnsNullForUnrelatedQuery() {
        val faqs = listOf(
            FaqEntity(
                id = 1,
                question = "What is cloud computing?",
                normalizedQuestion = TextNormalizer.normalize("What is cloud computing?"),
                answer = "Cloud answer",
                category = "Technology"
            )
        )
        assertNull(FaqMatcher.findBestMatch("zzzz unrelated nonsense xyz", faqs))
    }

    @Test
    fun findBestMatch_returnsNullWhenQueryContainsOnlyStopWords() {
        assertNull(FaqMatcher.findBestMatch("what is it and how do I", listOf(faq(1, "Cloud computing"))))
    }

    private fun faq(id: Long, question: String) = FaqEntity(
        id = id,
        question = question,
        normalizedQuestion = TextNormalizer.normalize(question),
        answer = "Answer $id",
        category = "Test"
    )
}
