package com.voxeet.testpromise.factory

import com.voxeet.promise.HandlerFactory
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.platform.commons.util.ReflectionUtils

class HandlerFactoryUninitializedTest {

    @BeforeEach
    fun rest() {
        // some value can be set when we expected none
        val handler = ReflectionUtils.findFields(HandlerFactory::class.java, {
            it.name == "handler"
        }, ReflectionUtils.HierarchyTraversalMode.TOP_DOWN).first()

        handler.isAccessible = true
        handler.set(null, null)
    }

    @Test
    fun `test non initialized factory`() {

        val handler = ReflectionUtils.findFields(HandlerFactory::class.java, {
            it.name == "handler"
        }, ReflectionUtils.HierarchyTraversalMode.TOP_DOWN).first()

        handler.isAccessible = true
        println("having value = " + handler.get(null))
        assertNull(handler.get(null))

        assertNotNull(HandlerFactory.getHandler())
        assertNotNull(handler.get(null))
    }
}
