package com.eliseo.kotlinexercise.presentation.anxiety

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliseo.kotlinexercise.domain.usecase.GetAnxietyClickCountUseCase
import com.eliseo.kotlinexercise.domain.usecase.RegisterAnxietyClickUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnxietyViewModel(
    private val getAnxietyClickCountUseCase: GetAnxietyClickCountUseCase,
    private val registerAnxietyClickUseCase: RegisterAnxietyClickUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnxietyUiState(isLoading = true))
    val uiState: StateFlow<AnxietyUiState> = _uiState.asStateFlow()

    init {
        loadCount()
    }

    fun loadCount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { getAnxietyClickCountUseCase() }
                .onSuccess { count ->
                    _uiState.update { it.copy(isLoading = false, clickCount = count) }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudo cargar el contador",
                        )
                    }
                }
        }
    }

    fun onAnxietyClick() {
        viewModelScope.launch {
            runCatching { registerAnxietyClickUseCase() }
                .onSuccess { count ->
                    _uiState.update { it.copy(clickCount = count, errorMessage = null) }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "No se pudo registrar el evento")
                    }
                }
        }
    }
}
