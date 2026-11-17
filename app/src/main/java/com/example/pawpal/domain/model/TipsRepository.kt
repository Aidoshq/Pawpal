package com.example.pawpal.domain.repository

import com.example.pawpal.domain.model.Tip

interface TipsRepository {
    suspend fun getItems(): List<Tip>
}