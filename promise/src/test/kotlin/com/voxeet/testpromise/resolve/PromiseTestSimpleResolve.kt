package com.voxeet.testpromise.resolve

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.await
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue

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
}
