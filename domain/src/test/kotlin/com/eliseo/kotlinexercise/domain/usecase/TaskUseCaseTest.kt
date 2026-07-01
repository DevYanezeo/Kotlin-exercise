package com.eliseo.kotlinexercise.domain.usecase

import com.eliseo.kotlinexercise.domain.model.TaskItem
import com.eliseo.kotlinexercise.domain.repository.TaskRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskUseCaseTest {
    @Test
    fun `getTasks returns tasks from repository`() =
        runTest {
            val expected =
                listOf(
                    TaskItem(id = "1", title = "Escribir tests", isCompleted = false),
                )
            val repository = FakeTaskRepository(tasks = expected)

            assertEquals(expected, GetTasksUseCase(repository)())
        }

    @Test
    fun `addTask appends new pending task`() =
        runTest {
            val repository =
                FakeTaskRepository(
                    tasks = listOf(TaskItem(id = "1", title = "Existente", isCompleted = false)),
                )

            val result = AddTaskUseCase(repository)("Nueva tarea")

            assertEquals(2, result.size)
            assertTrue(result.any { it.title == "Nueva tarea" && !it.isCompleted })
        }

    @Test
    fun `toggleTask flips completion state`() =
        runTest {
            val repository =
                FakeTaskRepository(
                    tasks = listOf(TaskItem(id = "1", title = "Configurar CI", isCompleted = false)),
                )

            val result = ToggleTaskUseCase(repository)("1")

            assertTrue(result.first().isCompleted)
        }

    private class FakeTaskRepository(
        private var tasks: List<TaskItem>,
    ) : TaskRepository {
        override suspend fun getTasks(): List<TaskItem> = tasks

        override suspend fun addTask(title: String): List<TaskItem> {
            if (title.isBlank()) return tasks
            tasks = tasks + TaskItem(id = "new", title = title, isCompleted = false)
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
}
