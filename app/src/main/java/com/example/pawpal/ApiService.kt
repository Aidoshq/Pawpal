package com.example.pawpal

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Quote(
    val id: Int,
    val quote: String,
    val author: String
)

data class QuotesResponse(
    val quotes: List<Quote>
)

interface ApiService {
    @GET("quotes")
    suspend fun getQuotes(): QuotesResponse
}

object RetrofitClient {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
