package com.voxeet.testpromise.subpromise.promise

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseSubRightAwayThrowError2 {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val mException = arrayOf<IllegalStateException?>(null)
        val latch = CountDownLatch(1)
        val equals = booleanArrayOf(false)
        val resolve_later = Promise.resolve(10).then { result: Int?, solver: Solver<Int?> ->
            try {
                throw IllegalStateException()
            } catch (e: IllegalStateException) {
                mException[0] = e
                solver.reject(e)
            }
        }
        println("executing test")
        Promise(resolve_later).error<Any> { error: Throwable ->
            println("error catched")
            equals[0] = mException[0] === error
            error.printStackTrace()
            latch.countDown()
        }

        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(equals[0] == true) { "Expected exception same..." }
    }
}
