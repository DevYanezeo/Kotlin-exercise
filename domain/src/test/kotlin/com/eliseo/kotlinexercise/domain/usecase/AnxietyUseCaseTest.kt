package com.eliseo.kotlinexercise.domain.usecase

import com.eliseo.kotlinexercise.domain.repository.AnxietyRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AnxietyUseCaseTest {
    @Test
    fun `getClickCount returns current count`() =
        runTest {
            val repository = FakeAnxietyRepository(count = 3)

            assertEquals(3, GetAnxietyClickCountUseCase(repository)())
        }

    @Test
    fun `registerClick increments and returns new count`() =
        runTest {
            val repository = FakeAnxietyRepository(count = 0)

            val result = RegisterAnxietyClickUseCase(repository)()

            assertEquals(1, result)
            assertEquals(1, repository.getClickCount())
        }

    private class FakeAnxietyRepository(
        private var count: Int,
    ) : AnxietyRepository {
        override suspend fun getClickCount(): Int = count

        override suspend fun registerClick(): Int {
            count += 1
            return count
        }
    }
}
