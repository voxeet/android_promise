package com.voxeet.testpromise.subpromise.promise

import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestWithSumDelayed {
    @Before
    fun setHandler() {
        Promise.setHandler(mockedhandler())
    }

    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val latch = CountDownLatch(1)
        val final_result = intArrayOf(0)
        val resolve_later = Promise { solver: Solver<Int?> ->
            object : Thread() {
                override fun run() {
                    println("sleeping...")
                    try {
                        sleep(20)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    solver.resolve(10 * 10)
                }
            }.run()
        }
        println("executing test")
        execute()
            .then { result: Int?, solver: Solver<Int?> ->
                solver.resolve(resolve_later
                    .then { new_result: Int?, solver1: Solver<Int?> ->
                        solver1.resolve(
                            new_result!! + result!!
                        )
                    }
                )
            }.then { result: Int? ->
                final_result[0] = result!!
            }
            .error { error: Throwable ->
                println("error catched")
                final_result[0] = 0
                error.printStackTrace()
                latch.countDown()
            }

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(final_result[0] == 110) { "Expected 110... having " + final_result[0] }
        println("having result " + final_result[0])
    }

    private fun execute(): Promise<Int?> {
        return Promise { solver: Solver<Int?> -> solver.resolve(10) }
    }
}
