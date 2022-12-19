package com.voxeet.testpromise.resolve

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.ThenVoid
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
    fun test() {
        val latch = CountDownLatch(1)
        var result = 0

        Promise.resolve(10).then { res: Int ->
            10 + res
        }.then(ThenVoid {
            result = 10 + it
            latch.countDown()
        }).error { error: Throwable ->
            println("error catched")
            result = 0
            error.printStackTrace()
            latch.countDown()
        }

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(result == 30) { "Expected 30... having $result" }
        println("having result $result")
    }

    @Test
    @Throws(InterruptedException::class)
    fun testSoloResolve() {
        val latch = CountDownLatch(1)
        var result = 0

        Promise.resolve(10).then {
            println("---------")
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
        check(result == 20) { "Expected 20... having $result" }
        println("having result $result")
    }
}
