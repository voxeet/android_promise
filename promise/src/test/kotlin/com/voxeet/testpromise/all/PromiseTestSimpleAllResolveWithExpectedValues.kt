package com.voxeet.testpromise.all

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.awaitNonNull
import com.voxeet.testpromise.mockedhandler
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertTrue

class PromiseTestSimpleAllResolveWithExpectedValues {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    fun test() = runTest {
        val expected = booleanArrayOf(
            true, false, true, true
        )

        val called = Promise.all(
            Promise.resolve(true),
            Promise.resolve(false),
            Promise.resolve(true),
            Promise.resolve(true)
        ).then { result: List<Boolean> ->
            if (result.size != expected.size) {
                throw IllegalStateException("Invalid result size")
            }
            assertArrayEquals(expected, result.toBooleanArray())
            true
        }.awaitNonNull()

        assertTrue(called)
    }
}
