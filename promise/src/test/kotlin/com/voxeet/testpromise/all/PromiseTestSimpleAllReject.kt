package com.voxeet.testpromise.all

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.await
import com.voxeet.promise.solve.Solver
import com.voxeet.promise.solve.ThenVoid
import com.voxeet.testpromise.mockedhandler
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.fail
import java.util.*

class PromiseTestSimpleAllReject {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    @Throws(InterruptedException::class)
    fun test() = runTest {
        var called = false
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
            ).then(ThenVoid<List<String?>> { result: List<String?> ->
                println(result.toTypedArray().contentToString())
                catched[0] = false
                called = true
            }).await()

            fail("expected error")
        } catch (e: NullPointerException) {
            //expected
        }
    }
}
