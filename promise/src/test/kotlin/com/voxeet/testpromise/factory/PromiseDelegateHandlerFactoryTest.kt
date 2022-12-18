package com.voxeet.testpromise.factory

import com.voxeet.promise.HandlerFactory
import com.voxeet.promise.Promise
import com.voxeet.testpromise.mockedhandler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PromiseDelegateHandlerFactoryTest {

    @Test
    fun `test initializing the factory`() {
        val handler = mockedhandler()

        Promise.setHandler(handler)
        assertEquals(handler, HandlerFactory.getHandler())
    }
}
