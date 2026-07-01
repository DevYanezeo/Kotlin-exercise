package com.eliseo.kotlinexercise.domain.usecase

import com.eliseo.kotlinexercise.domain.model.TaskItem
import com.eliseo.kotlinexercise.domain.repository.TaskRepository

class AddTaskUseCase(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(title: String): List<TaskItem> = repository.addTask(title.trim())
}
