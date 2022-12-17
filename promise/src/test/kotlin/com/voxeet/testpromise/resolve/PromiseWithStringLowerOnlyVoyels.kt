package com.voxeet.testpromise.resolve

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.PromiseExec
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseWithStringLowerOnlyVoyels {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val latch = CountDownLatch(1)
        val equals = booleanArrayOf(false)
        Promise { solver: Solver<String?> ->
            solver.resolve(
                "TintInTotoTutu"
            )
        }.then { result: String?, solver: Solver<String?> ->
            solver.resolve(
                result!!.lowercase(Locale.getDefault())
            )
        }.then(PromiseExec { result: String?, solver: Solver<String> ->
            solver.resolve(
                result!!.replace("[^aeiouy]".toRegex(), "")
            )
        } as PromiseExec<String?, String>).then { result: String ->
            println("result $result")
            equals[0] = "iioouu" == result
            latch.countDown()
        }.execute()


        //6s are enough
        latch.await(6, TimeUnit.SECONDS)
        check(equals[0]) { "Expected same result..." }
    }
}
