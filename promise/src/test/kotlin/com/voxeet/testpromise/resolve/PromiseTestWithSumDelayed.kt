package com.voxeet.testpromise.resolve

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestWithSumDelayed {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    fun test() {
        val latch = CountDownLatch(1)
        var result = 0

        Promise.resolve(10).then { res: Int?, solver: Solver<Int> ->
            object : Thread() {
                override fun run() {
                    try {
                        sleep(200)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    solver.resolve(10 + res!!)
                }
            }.start()
        }.then {
            result = 10 + it
            latch.countDown()
        }.error { error: Throwable ->
            println("error catched")
            result = 0
            error.printStackTrace()
            latch.countDown()
        }

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        assertEquals(30, result)
    }
}
