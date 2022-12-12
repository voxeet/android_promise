package com.voxeet.testpromise

import com.voxeet.promise.Promise
import com.voxeet.promise.await
import com.voxeet.promise.awaitNonNull
import com.voxeet.promise.solve.ThenVoid
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PromiseKtxTest {

    @BeforeEach
    fun onBeforeEach() {
        Promise.setHandler(mockedhandler())
    }

    @Test
    fun `test running regular promise`() = runTest {
        val value = Promise.resolve(true).await()

        assertTrue(value!!)

        var called = false
        val anotherValue = Promise<Boolean> { solver ->
            mockedhandler().postDelayed({
                called = true
                solver.resolve(true)
            }, 2000)
        }.await()

        assertTrue(anotherValue!!)
        assertTrue(called)
    }

    @Test
    fun `test making an exception`() = runTest {
        try {
            val value = Promise.resolve(null).awaitNonNull()
            fail("this should have failed")
        } catch (e: java.lang.IllegalStateException) {
            // expected
        }

        try {
            val value = Promise.reject<Boolean>(java.lang.IllegalStateException("exception !")).await()
            fail("this should have failed")
        } catch (e: java.lang.IllegalStateException) {
            // expected
        }

        var called = false
        try {
            val value = Promise<Boolean> { solver ->
                mockedhandler().postDelayed({
                    called = true
                    solver.reject(java.lang.IllegalStateException("some exception"))
                }, 2000)
            }.await()

            fail("this should have failed")
        } catch (e: java.lang.IllegalStateException) {
            // expected
        }
        assertTrue(called)
    }

    @Test
    fun `check that resolving from a void will still be ok`() = runTest {
        try {
            var called = false
            val value = Promise { solver ->
                called = true
                solver.resolve(true)
            }.then(ThenVoid {
                //nothing will be returned, value is typed void
            }).await()
            assertTrue(called)
        } catch (e: Throwable) {
            fail("this shouldn't have failed")
        }

        try {
            var called = false
            val value = Promise { solver ->
                mockedhandler().postDelayed({
                    called = true
                    solver.resolve(true)
                }, 2000)
            }.then(ThenVoid {
                //nothing will be returned, value is typed void
            }).await()

            assertTrue(called)
        } catch (e: Throwable) {
            fail("this shouldn't have failed")
        }
    }
}