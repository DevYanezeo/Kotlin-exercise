package com.eliseo.kotlinexercise.presentation.todo

import com.eliseo.kotlinexercise.domain.model.TaskItem

data class TodoUiState(
    val isLoading: Boolean = false,
    val tasks: List<TaskItem> = emptyList(),
    val newTaskTitle: String = "",
    val errorMessage: String? = null,
) {
    val pendingCount: Int get() = tasks.count { !it.isCompleted }
    val completedCount: Int get() = tasks.count { it.isCompleted }
}
