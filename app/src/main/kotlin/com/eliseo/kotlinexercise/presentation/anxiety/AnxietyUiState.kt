package com.eliseo.kotlinexercise.presentation.anxiety

data class AnxietyUiState(
    val isLoading: Boolean = false,
    val clickCount: Int = 0,
    val errorMessage: String? = null,
)
