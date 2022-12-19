package com.voxeet.testpromise.resolve

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.solve.ThenValue
import com.voxeet.testpromise.mockedhandler
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PromiseTransformationTest {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    fun addition_isCorrect() {
        var result = 0
        val latch = CountDownLatch(1)
        val promise = Promise.resolve(false)
        promise.then { "test2" }
            .then(ThenValue { it.length }).then {
                result = it
                latch.countDown()
            }.execute()

        //6s are enough
        latch.await(
            6, TimeUnit.SECONDS
        )
        assertEquals(5, result)
    }
}