package com.eliseo.kotlinexercise.domain.usecase

import com.eliseo.kotlinexercise.domain.model.TaskItem
import com.eliseo.kotlinexercise.domain.repository.TaskRepository

class ToggleTaskUseCase(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(taskId: String): List<TaskItem> = repository.toggleTask(taskId)
}
