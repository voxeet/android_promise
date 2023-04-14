package com.voxeet.testpromise.all

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.await
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import com.voxeet.testpromise.registerConfigurationForNoRethrowResolve
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.fail
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class PromiseTestSimpleAllReject {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
        registerConfigurationForNoRethrowResolve()
    }

    @Test
    fun test() = runTest {
        val catched = booleanArrayOf(true)

        try {
            Promise.all(
                Promise { solver: Solver<String?> ->
                    try {
                        throw NullPointerException("oops")
                    } catch (e: Exception) {
                        solver.reject(e)
                    }
                }, Promise.resolve("called 2")
            ).then { result: List<String?> ->
                println(result.toTypedArray().contentToString())
                catched[0] = false
                true
            }.await()

            fail("expected error")
        } catch (e: NullPointerException) {
            //expected
        }
    }
}
