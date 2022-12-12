package com.voxeet.testpromise.subpromise.promise

import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.promise.solve.ThenVoid
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestWithSumDelayed2 {
    @Before
    fun setHandler() {
        Promise.setHandler(mockedhandler())
    }

    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val latch = CountDownLatch(1)
        val final_result = intArrayOf(0)
        println("executing test")
        Promise { solver: Solver<Int?> ->
            solver.resolve(
                Promise { solver1: Solver<Int?> ->
                    object : Thread() {
                        override fun run() {
                            println("sleeping...")
                            try {
                                sleep(20)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            solver1.resolve(10 * 10)
                        }
                    }.start()
                }
            )
        }.then(ThenVoid<Int?> { result: Int? ->
            final_result[0] = result!!
        }).error { error: Throwable ->
            println("error catched")
            final_result[0] = 0
            error.printStackTrace()
            latch.countDown()
        }

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(final_result[0] == 100) { "Expected 100... having " + final_result[0] }
        println("having result " + final_result[0])
    }
}
