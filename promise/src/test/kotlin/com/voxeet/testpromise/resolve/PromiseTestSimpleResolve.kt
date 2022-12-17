package com.voxeet.testpromise.resolve

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestSimpleResolve {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val latch = CountDownLatch(1)
        val catched = booleanArrayOf(true)
        println("executing test")
        Promise { solver: Solver<String?> ->
            catched[0] = false
            solver.resolve("called")
            latch.countDown()
        }.execute()

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(!catched[0]) { "Expected 0 error..." }
    }
}
