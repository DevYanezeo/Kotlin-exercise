package com.eliseo.kotlinexercise.domain.repository

import com.eliseo.kotlinexercise.domain.model.TaskItem

interface TaskRepository {
    suspend fun getTasks(): List<TaskItem>

    suspend fun addTask(title: String): List<TaskItem>

    suspend fun toggleTask(taskId: String): List<TaskItem>
}
