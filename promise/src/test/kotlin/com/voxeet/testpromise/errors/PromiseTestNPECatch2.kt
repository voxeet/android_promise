package com.voxeet.testpromise.errors

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTestNPECatch2 {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val latch = CountDownLatch(1)
        val catched = booleanArrayOf(false)
        println("executing test")
        Promise { solver: Solver<String?> ->
            solver.resolve((null as String?)!!.lowercase(Locale.getDefault()))
        }.then { result: String?, solver: Solver<Void?>? ->
            println("you should not see this")
            catched[0] = false
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
