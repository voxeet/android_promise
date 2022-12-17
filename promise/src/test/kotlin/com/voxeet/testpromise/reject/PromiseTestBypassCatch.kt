package com.voxeet.testpromise.reject

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestBypassCatch {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    fun test() {
        val latch = CountDownLatch(1)
        val catched = booleanArrayOf(false)
        println("executing test")
        Promise { solver: Solver<String?> ->
            try {
                throw IllegalAccessException("Yup")
            } catch (e: IllegalAccessException) {
                solver.reject(e)
            }
            println("you should see this :p")
        }.then { result: String?, solver: Solver<String?> ->
            solver.resolve(
                "but not this ^^"
            )
        }.then { result: String? ->
            println(result)
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
