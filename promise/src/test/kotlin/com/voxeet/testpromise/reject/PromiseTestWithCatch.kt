package com.voxeet.testpromise.reject

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestWithCatch {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val latch = CountDownLatch(1)
        val catched = booleanArrayOf(false)
        println("executing test")
        Promise { solver: Solver<String?> ->
            println("onCall")
            solver.resolve("result!!")
        }.then { result: String?, solver: Solver<Int?> ->
            solver.reject(
                IllegalStateException()
            )
        }.then { result: Int?, solver: Solver<Void?>? ->
            println("should not be seen")
            latch.countDown()
        }.error { error: Throwable ->
            println("error catched")
            catched[0] = true
            error.printStackTrace()
            latch.countDown()
        }

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(catched[0]) { "Expected error..." }
    }
}
