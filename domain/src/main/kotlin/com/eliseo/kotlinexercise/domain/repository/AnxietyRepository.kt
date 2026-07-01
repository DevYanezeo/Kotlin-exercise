package com.eliseo.kotlinexercise.domain.repository

interface AnxietyRepository {
    suspend fun getClickCount(): Int

    suspend fun registerClick(): Int
}
