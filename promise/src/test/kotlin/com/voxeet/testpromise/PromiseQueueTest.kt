package com.voxeet.testpromise

import com.voxeet.promise.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@OptIn(ExperimentalCoroutinesApi::class)
class PromiseQueueTest {

    @BeforeEach
    fun beforeTest() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    fun `test posting an exception in the queue`() = runTest {
        val handlers = mockedhandler()
        val queue = PromiseQueue()
        var calls = 0

        val promises: List<Promise<Boolean>> = listOf(
            queue.enqueue {
                handlers.postDelayed({
                    // we check that the 2nd queued promise was not called yet
                    assertEquals(0, calls)
                    calls++
                    it.reject(IllegalStateException("exception !"))
                }, 2000)
            },
            queue.enqueue {
                calls++
                it.resolve(true)
            },
        )

        try {
            promises[0].await()
            fail("well.. unexpected")
        } catch (e: IllegalStateException) {
            //expected
        }

        assertTrue(promises[1].awaitNonNull())
        assertEquals(2, calls)
    }

    @Test
    fun `test posting in the queue`() = runTest {
        val handlers = mockedhandler()
        val queue = PromiseQueue()
        var calls = 0

        val promises: List<Promise<Boolean>> = listOf(
            queue.enqueue {
                handlers.postDelayed({
                    // we check that the 2nd queued promise was not called yet
                    assertEquals(0, calls)

                    calls++
                    it.resolve(true)
                }, 2000)
            },
            queue.enqueue {
                calls++
                it.resolve(true)
            },
        )

        Promise.all(promises).await()

        assertEquals(2, calls)
    }
}