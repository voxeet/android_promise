package com.voxeet.testpromise.errors

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.awaitNonNull
import com.voxeet.testpromise.mockedhandler
import com.voxeet.testpromise.registerConfigurationForNoRethrowResolve
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.fail
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class PromiseTestNPECatch {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
        registerConfigurationForNoRethrowResolve()
    }

    @Test
    fun test() = runTest {
        try {
            Promise.resolve(null as String)
                .then { result: String ->
                    //explode right here
                    result.lowercase(Locale.getDefault())
                }
                .then {
                    println("you should not see this $it")
                    true
                }.awaitNonNull()

            fail("this should have failed right above")
        } catch (e: NullPointerException) {
            // expected
        }
    }
}
