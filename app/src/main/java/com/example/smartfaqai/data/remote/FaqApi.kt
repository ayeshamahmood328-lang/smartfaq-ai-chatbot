package com.example.smartfaqai.data.remote

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class FaqRequest(val question: String)
data class FaqResponse(val answer: String, val category: String? = null)

interface FaqApi {
    @POST("faq/ask")
    suspend fun ask(@Body request: FaqRequest): Response<FaqResponse>
}

object FaqApiClient {
    // Offline FAQ matching is the primary engine; Retrofit is wired for architecture completeness.
    val api: FaqApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://example.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FaqApi::class.java)
    }
}
