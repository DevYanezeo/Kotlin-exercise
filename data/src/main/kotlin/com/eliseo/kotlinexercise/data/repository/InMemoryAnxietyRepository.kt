package com.eliseo.kotlinexercise.data.repository

import com.eliseo.kotlinexercise.domain.repository.AnxietyRepository

class InMemoryAnxietyRepository : AnxietyRepository {
    private var clickCount = 0

    override suspend fun getClickCount(): Int = clickCount

    override suspend fun registerClick(): Int {
        clickCount += 1
        return clickCount
    }
}
