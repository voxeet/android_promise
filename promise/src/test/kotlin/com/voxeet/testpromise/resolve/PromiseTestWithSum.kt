package com.voxeet.testpromise.resolve

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestWithSum {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val latch = CountDownLatch(1)
        val final_result = intArrayOf(0)
        println("executing test")
        execute().then { result: Int?, solver: Solver<Int?> ->
            solver.resolve(
                10 + result!!
            )
        }.then { result: Int?, solver: Solver<Int?>? ->
            final_result[0] = 10 + result!!
            latch.countDown()
        }.error { error: Throwable ->
            println("error catched")
            final_result[0] = 0
            error.printStackTrace()
            latch.countDown()
        }

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(final_result[0] == 30) { "Expected 30... having " + final_result[0] }
        println("having result " + final_result[0])
    }

    @Test
    @Throws(InterruptedException::class)
    fun testSoloResolve() {
        val latch = CountDownLatch(1)
        val final_result = intArrayOf(0)
        println("executing test")
        execute().then { result: Int?, solver: Solver<Int?>? ->
            println("---------")
            final_result[0] = 10 + result!!
            latch.countDown()
        }.error { error: Throwable ->
            println("error catched")
            final_result[0] = 0
            error.printStackTrace()
            latch.countDown()
        }

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(final_result[0] == 20) { "Expected 20... having " + final_result[0] }
        println("having result " + final_result[0])
    }

    private fun execute(): Promise<Int?> {
        return Promise { solver: Solver<Int?> -> solver.resolve(10) }
    }
}
