package com.voxeet.testpromise.resolve

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.Solver
import com.voxeet.promise.solve.ThenValue
import com.voxeet.testpromise.mockedhandler
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTransformationTest {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    fun addition_isCorrect() {
        val test_result = intArrayOf(0)
        val latch = CountDownLatch(1)
        val promise = Promise.resolve(false)
        promise.then { result: Boolean?, solver: Solver<String?> ->
            solver.resolve(
                "test2"
            )
        }.then(ThenValue<String?, Int?> { result: String? ->
            result!!.length
        }).then {
            test_result[0] = it!!
            latch.countDown()
        }.execute()

        //6s are enough
        latch.await(
            6, TimeUnit.SECONDS
        )
        Assert.assertEquals(5, test_result[0])
    }
}