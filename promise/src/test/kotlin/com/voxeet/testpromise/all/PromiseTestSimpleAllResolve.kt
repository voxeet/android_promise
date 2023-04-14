package com.voxeet.testpromise.all

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.await
import com.voxeet.promise.awaitNonNull
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import com.voxeet.testpromise.registerConfigurationForNoRethrowResolve
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue

class PromiseTestSimpleAllResolve {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
        registerConfigurationForNoRethrowResolve()
    }

    @Test
    fun test() = runTest {
        val called = Promise.all(
            Promise { solver: Solver<String?> ->
                solver.resolve("called 1")
            },
            Promise.resolve("called 2")
        ).then { result: List<String?> ->
            println("execution result = ")
            println(result.toTypedArray().contentToString())
            false
        }.then { !it }.awaitNonNull()

        assertTrue(called)
    }
}
