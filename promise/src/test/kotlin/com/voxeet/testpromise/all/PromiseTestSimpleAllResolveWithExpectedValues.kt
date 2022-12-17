package com.voxeet.testpromise.all

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.ThenVoid
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestSimpleAllResolveWithExpectedValues {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val latch = CountDownLatch(1)
        val catched = booleanArrayOf(false)
        val expected = booleanArrayOf(
            true, false, true, true
        )
        println("executing test")
        Promise.all(
            Promise.resolve(true),
            Promise.resolve(false),
            Promise.resolve(true),
            Promise.resolve(true)
        ).then(ThenVoid<List<Boolean>> { result: List<Boolean> ->
            if (result.size != expected.size) {
                throw IllegalStateException("Invalid result size")
            } else {
                var index = 0
                while (index < result.size) {
                    if (result[index] != expected[index]) {
                        latch.countDown()
                        throw IllegalStateException("Invalid result value at index $index")
                    }
                    index++
                }
            }
            latch.countDown()
        }).error { error: Throwable ->
            error.printStackTrace()
            catched[0] = true
            latch.countDown()
        }

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(!catched[0]) { "Expected 0 error..." }
    }
}
