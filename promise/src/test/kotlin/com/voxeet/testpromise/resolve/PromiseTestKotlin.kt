package com.voxeet.testpromise.resolve

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.await
import com.voxeet.testpromise.mockedhandler
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class PromiseTestKotlin {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    fun testCallingUsingFunction1() = runTest {
        var called = false

        val void = Promise { solver ->
            solver.resolve(true)
        }.then {
            called = true
            assertTrue(it)
        }.await()

        assertTrue(called)
        assertEquals(Unit, void)
    }
}
