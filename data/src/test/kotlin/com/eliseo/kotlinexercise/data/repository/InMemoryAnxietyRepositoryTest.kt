package com.eliseo.kotlinexercise.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class InMemoryAnxietyRepositoryTest {
    @Test
    fun `starts at zero clicks`() =
        runTest {
            val repository = InMemoryAnxietyRepository()

            assertEquals(0, repository.getClickCount())
        }

    @Test
    fun `registerClick increments count`() =
        runTest {
            val repository = InMemoryAnxietyRepository()

            assertEquals(1, repository.registerClick())
            assertEquals(2, repository.registerClick())
            assertEquals(2, repository.getClickCount())
        }
}
