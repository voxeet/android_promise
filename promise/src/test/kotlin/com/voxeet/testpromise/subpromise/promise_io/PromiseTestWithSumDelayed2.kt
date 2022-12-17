package com.voxeet.testpromise.subpromise.promise_io

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestWithSumDelayed2 {
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
        execute()
            .then { result: Int?, solver: Solver<Int?> ->
                //testing here resolving from promise in a chain
                solver.resolve(Promise { solver1: Solver<Int?> ->
                    solver1.resolve(
                        result
                    )
                })
            }
            .then { final_result[0] = it!! }
            .error { error: Throwable ->
                println("error catched")
                final_result[0] = 0
                error.printStackTrace()
                latch.countDown()
            }

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(final_result[0] == 10) { "Expected 10... having " + final_result[0] }
        println("having result " + final_result[0])
    }

    private fun execute(): Promise<Int?> {
        return Promise { solver: Solver<Int?> -> solver.resolve(10) }
    }
}
