package com.example.smartfaqai.ui.category

data class CategoryInfo(
    val name: String,
    val description: String
) {
    companion object {
        private val items = listOf(
            CategoryInfo(
                "Technology",
                "Practical guidance for devices, internet safety, software, AI, privacy, and everyday digital tools."
            ),
            CategoryInfo(
                "Cloud Computing",
                "Learn how the cloud works, from SaaS, IaaS, and PaaS to storage, security, and serverless computing."
            ),
            CategoryInfo(
                "AI",
                "Understand artificial intelligence: machine learning, neural networks, chatbots, and generative AI."
            ),
            CategoryInfo(
                "Android",
                "Everything about Android phones and development, from APKs and updates to Kotlin and Android Studio."
            ),
            CategoryInfo(
                "Education",
                "Study smarter with proven learning techniques, research guidance, exam preparation, and academic skills."
            ),
            CategoryInfo(
                "Health",
                "Clear general wellness information covering sleep, nutrition, exercise, prevention, and when to seek care."
            ),
            CategoryInfo(
                "Banking",
                "Understand accounts, cards, interest, transfers, credit, fraud prevention, and safe money management."
            ),
            CategoryInfo(
                "Shopping",
                "Make confident purchases with guidance on pricing, returns, online safety, warranties, and consumer choices."
            ),
            CategoryInfo(
                "General",
                "Useful answers for productivity, careers, communication, organization, personal growth, and daily life."
            )
        )

        fun forName(name: String): CategoryInfo =
            items.firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: CategoryInfo(name, "Browse useful offline questions and answers in $name.")
    }
}
