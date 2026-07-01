package com.eliseo.kotlinexercise.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliseo.kotlinexercise.domain.usecase.AddTaskUseCase
import com.eliseo.kotlinexercise.domain.usecase.GetTasksUseCase
import com.eliseo.kotlinexercise.domain.usecase.ToggleTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val toggleTaskUseCase: ToggleTaskUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TodoUiState(isLoading = true))
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { getTasksUseCase() }
                .onSuccess { tasks ->
                    _uiState.update { it.copy(isLoading = false, tasks = tasks) }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudieron cargar las tareas",
                        )
                    }
                }
        }
    }

    fun onNewTaskTitleChange(title: String) {
        _uiState.update { it.copy(newTaskTitle = title, errorMessage = null) }
    }

    fun addTask() {
        val title = _uiState.value.newTaskTitle
        if (title.isBlank()) return

        viewModelScope.launch {
            runCatching { addTaskUseCase(title) }
                .onSuccess { tasks ->
                    _uiState.update {
                        it.copy(tasks = tasks, newTaskTitle = "", errorMessage = null)
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "No se pudo agregar la tarea")
                    }
                }
        }
    }

    fun toggleTask(taskId: String) {
        viewModelScope.launch {
            runCatching { toggleTaskUseCase(taskId) }
                .onSuccess { tasks ->
                    _uiState.update { it.copy(tasks = tasks, errorMessage = null) }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "No se pudo actualizar la tarea")
                    }
                }
        }
    }
}
