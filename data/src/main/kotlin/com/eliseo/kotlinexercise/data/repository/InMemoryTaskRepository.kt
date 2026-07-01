package com.eliseo.kotlinexercise.data.repository

import com.eliseo.kotlinexercise.domain.model.TaskItem
import com.eliseo.kotlinexercise.domain.repository.TaskRepository
import java.util.UUID

class InMemoryTaskRepository : TaskRepository {
    private var tasks =
        listOf(
            TaskItem(id = "1", title = "Configurar Ktlint en CI", isCompleted = true),
            TaskItem(id = "2", title = "Documentar reglas en AGENTS.md", isCompleted = false),
        )

    override suspend fun getTasks(): List<TaskItem> = tasks

    override suspend fun addTask(title: String): List<TaskItem> {
        if (title.isBlank()) return tasks
        val newTask =
            TaskItem(
                id = UUID.randomUUID().toString(),
                title = title,
                isCompleted = false,
            )
        tasks = tasks + newTask
        return tasks
    }

    override suspend fun toggleTask(taskId: String): List<TaskItem> {
        tasks =
            tasks.map { task ->
                if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
            }
        return tasks
    }
}
