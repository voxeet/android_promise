package com.voxeet.testpromise

import android.os.Handler
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

fun mockedhandler(): Handler {
    val mockHandler: Handler = mock()
    `when`(mockHandler.post(any())).thenAnswer { invocation ->
        val runnable: Runnable = invocation.getArgument(0)
        runnable.run()
        null
    }

    `when`(mockHandler.postDelayed(any(), any())).thenAnswer { invocation ->
        val runnable: Runnable = invocation.getArgument(0)
        val delayed: Long = invocation.getArgument(1)

        Thread {
            Thread.sleep(delayed)
            runnable.run()
        }.start()

        null
    }
    return mockHandler
}