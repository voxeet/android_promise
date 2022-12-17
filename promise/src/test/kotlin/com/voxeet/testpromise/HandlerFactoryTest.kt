package com.voxeet.testpromise

import com.voxeet.promise.HandlerFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class HandlerFactoryTest {

    @Test
    fun `test initializing the factory`() {
        val handler = mockedhandler()

        HandlerFactory.setHandler(handler)
        assertEquals(handler, HandlerFactory.getHandler())
    }

    @Test
    fun `test non initialized factory`() {
        assertNotNull(HandlerFactory.getHandler())
    }
}
