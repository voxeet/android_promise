package com.voxeet.testpromise.errors

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.promise.await
import com.voxeet.promise.solve.Solver
import com.voxeet.testpromise.mockedhandler
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.fail
import java.util.*

class PromiseTestNPECatch2 {
    @Before
    fun setHandler() {
        HandlerFactory.setHandler(mockedhandler())
    }

    @Test
    fun test() = runTest {
        try {
            Promise { solver: Solver<String?> ->
                solver.resolve(
                    (null as String).lowercase(Locale.getDefault())
                )
            }.then { result: String? ->
                println("you should not see this")
                result
            }.await()

            fail("expected a failure")
        } catch (e: NullPointerException) {
            // expected
        }
    }
}
