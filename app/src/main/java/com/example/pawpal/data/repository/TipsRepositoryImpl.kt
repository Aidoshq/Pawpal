package com.example.pawpal.data.repository

import com.example.pawpal.RetrofitClient
import com.example.pawpal.domain.model.Tip
import com.example.pawpal.domain.repository.TipsRepository

class TipsRepositoryImpl : TipsRepository {

    override suspend fun getItems(): List<Tip> {
        val response = RetrofitClient.api.getQuotes()

        return response.quotes.map { quote ->
            Tip(
                id = quote.id,
                text = quote.quote,
                author = quote.author
            )
        }
    }
}
