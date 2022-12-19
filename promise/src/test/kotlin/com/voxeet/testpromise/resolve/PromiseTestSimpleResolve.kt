package com.voxeet.testpromise.resolve

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.await
import com.voxeet.promise.solve.Solver
import com.voxeet.promise.solve.ThenVoid
import com.voxeet.testpromise.mockedhandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PromiseTestSimpleResolve {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    fun test() = runTest {
        var called = false

        Promise { solver: Solver<String?> ->
            called = true

            solver.resolve(null)
        }.await()

        assertTrue(called)
    }

    @Test
    fun `test then void`() = runTest {
        var called = false

        val void = Promise { solver: Solver<String?> -> solver.resolve(null) }
            .then { it }
            .then(ThenVoid {
                called = true
            }).await()

        assertTrue(called)
        assertNull(void)
    }
}
