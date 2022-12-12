package com.voxeet.testpromise.all

import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.promise.solve.ThenVoid
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestSimpleAllReject {
    @Before
    fun setHandler() {
        Promise.setHandler(mockedhandler())
    }

    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val latch = CountDownLatch(1)
        val catched = booleanArrayOf(true)

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
            latch.countDown()
        }).error { error: Throwable ->
            error.printStackTrace()
            catched[0] = true
            latch.countDown()
        }

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(catched[0]) { "Expected an error..." }
    }
}
