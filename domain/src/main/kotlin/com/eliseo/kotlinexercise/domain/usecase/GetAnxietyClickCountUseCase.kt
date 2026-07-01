package com.eliseo.kotlinexercise.domain.usecase

import com.eliseo.kotlinexercise.domain.repository.AnxietyRepository

class GetAnxietyClickCountUseCase(
    private val repository: AnxietyRepository,
) {
    suspend operator fun invoke(): Int = repository.getClickCount()
}
