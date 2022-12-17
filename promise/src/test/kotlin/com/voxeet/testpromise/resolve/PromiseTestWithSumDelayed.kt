package com.voxeet.testpromise.resolve

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.*
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestWithSumDelayed {
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
        execute().then(PromiseExec { result: Int?, solver: Solver<Int> ->
            object : Thread() {
                override fun run() {
                    println("sleeping...")
                    try {
                        sleep(200)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    solver.resolve(10 + result!!)
                }
            }.start()
        } as PromiseExec<Int?, Int>).then { result: Int ->
            final_result[0] = 10 + result
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

    private fun execute(): Promise<Int?> {
        return Promise { solver: Solver<Int?> -> solver.resolve(10) }
    }
}
