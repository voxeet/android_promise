package com.voxeet.testpromise.factory

import com.voxeet.promise.HandlerFactory
import com.voxeet.testpromise.mockedhandler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.platform.commons.util.ReflectionUtils

class HandlerFactoryTest {

    @Test
    fun `test initializing the factory`() {
        val handler = mockedhandler()

        HandlerFactory.setHandler(handler)
        assertEquals(handler, HandlerFactory.getHandler())
    }
}
