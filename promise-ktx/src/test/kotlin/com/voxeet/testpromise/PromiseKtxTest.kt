package com.voxeet.testpromise

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.await
import com.voxeet.promise.awaitNonNull
import com.voxeet.promise.awaitNullable
import com.voxeet.promise.solve.ThenValue
import com.voxeet.promise.solve.ThenVoid
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PromiseKtxTest {

    @BeforeEach
    fun onBeforeEach() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    fun `test running regular promise`() = runTest {
        val value = Promise.resolve(true).await()

        assertTrue(value!!)

        var called = false
        var calledNullable = false
        var calledNonNull = false
        val anotherValue = Promise<Boolean> { solver ->
            mockedhandler().postDelayed({
                called = true
                solver.resolve(true)
            }, 2000)
        }.awaitNonNull()

        val anotherValueFromNullable = Promise<Boolean?> { solver ->
            mockedhandler().postDelayed({
                calledNullable = true
                solver.resolve(null)
            }, 2000)
        }.awaitNullable()

        val anotherValueFromNonNull = Promise<Boolean> { solver ->
            mockedhandler().postDelayed({
                calledNonNull = true
                solver.resolve(true)
            }, 2000)
        }.awaitNonNull()

        assertTrue(anotherValue)
        assertNull(anotherValueFromNullable)
        assertTrue(anotherValueFromNonNull)

        assertTrue(called)
        assertTrue(calledNullable)
        assertTrue(calledNonNull)

        try {
            Promise.resolve(false).awaitNonNull()
        } catch (e: NullPointerException) {
            fail("this should have worked")
        }

        try {
            Promise.resolve(null as String?).awaitNullable()
        } catch (e: NullPointerException) {
            fail("this should have worked")
        }

        try {
            Promise.resolve<Boolean>(true).then(ThenValue { it }).awaitNonNull()
        } catch (e: NullPointerException) {
            fail("this should have worked")
        }
    }

    @Test
    fun `test making an exception`() = runTest {
        try {
            Promise.resolve(null as String).awaitNonNull()
            fail("this should have failed")
        } catch (e: NullPointerException) {
            // expected
        }

        try {
            val str = Promise.resolve(null as String?).awaitNullable()
        } catch (e: Throwable) {
            fail("this shouldn't have failed")
        }

        try {
            Promise.resolve<Boolean?>(null).then(ThenValue { it }).awaitNullable()
        } catch (e: Throwable) {
            fail("this shouldn't have failed")
        }

        try {
            Promise.resolve<Boolean>(null).then(ThenValue { it }).awaitNonNull()
            fail("this should have failed")
        } catch (e: NullPointerException) {
            // expected
        }

        try {
            Promise.reject<Boolean>(java.lang.IllegalStateException("exception !")).awaitNonNull()
            fail("this should have failed")
        } catch (e: java.lang.IllegalStateException) {
            // expected
        }

        try {
            Promise.reject<Boolean>(java.lang.IllegalStateException("exception !")).awaitNullable()
            fail("this should have failed")
        } catch (e: java.lang.IllegalStateException) {
            // expected
        }

        try {
            Promise.reject<Boolean>(java.lang.IllegalStateException("exception !")).await()
            fail("this should have failed")
        } catch (e: java.lang.IllegalStateException) {
            // expected
        }

        var called = false
        try {
            Promise<Boolean> { solver ->
                mockedhandler().postDelayed({
                    called = true
                    solver.reject(java.lang.IllegalStateException("some exception"))
                }, 2000)
            }.awaitNullable()

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
            Promise { solver ->
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
            Promise { solver ->
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