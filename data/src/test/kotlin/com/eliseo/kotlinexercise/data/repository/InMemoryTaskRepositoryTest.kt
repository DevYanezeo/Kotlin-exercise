package com.eliseo.kotlinexercise.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryTaskRepositoryTest {
    @Test
    fun `getTasks returns seeded tasks`() =
        runTest {
            val repository = InMemoryTaskRepository()

            val tasks = repository.getTasks()

            assertEquals(2, tasks.size)
        }

    @Test
    fun `addTask creates pending task`() =
        runTest {
            val repository = InMemoryTaskRepository()

            val tasks = repository.addTask("Revisar PR")

            assertEquals(3, tasks.size)
            assertTrue(tasks.any { it.title == "Revisar PR" && !it.isCompleted })
        }

    @Test
    fun `toggleTask marks task as completed`() =
        runTest {
            val repository = InMemoryTaskRepository()
            val pending = repository.getTasks().first { !it.isCompleted }

            val updated = repository.toggleTask(pending.id)

            assertTrue(updated.first { it.id == pending.id }.isCompleted)
        }
}
