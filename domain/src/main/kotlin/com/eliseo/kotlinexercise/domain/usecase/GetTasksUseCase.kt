package com.eliseo.kotlinexercise.domain.usecase

import com.eliseo.kotlinexercise.domain.model.TaskItem
import com.eliseo.kotlinexercise.domain.repository.TaskRepository

class GetTasksUseCase(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(): List<TaskItem> = repository.getTasks()
}
