package com.voxeet.testpromise.subpromise.promise

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import com.voxeet.testpromise.registerConfigurationForNoRethrowResolve
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseSubRightAwayThrowError {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
        registerConfigurationForNoRethrowResolve()
    }

    @Test
    fun test() {
        val mException = arrayOf<IllegalStateException?>(null)
        val latch = CountDownLatch(1)
        val equals = booleanArrayOf(false)
        val resolve_later = Promise { solver: Solver<Int?> ->
            try {
                throw IllegalStateException()
            } catch (e: IllegalStateException) {
                mException[0] = e
                solver.reject(e)
            }
        }
        println("executing test")
        Promise { solver: Solver<Int?> -> solver.resolve(resolve_later) }
            .error { error: Throwable ->
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
