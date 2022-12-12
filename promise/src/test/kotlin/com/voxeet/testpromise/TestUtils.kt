package com.voxeet.testpromise

import android.os.Handler
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val mainThread = Executors.newSingleThreadScheduledExecutor()

fun mockedhandler(): Handler {
    val mockHandler: Handler = mock()
    `when`(mockHandler.post(any())).thenAnswer { invocation ->
        val runnable: Runnable = invocation.getArgument(0)
        mainThread.execute(runnable)

        null
    }

    `when`(mockHandler.postDelayed(any(), any())).thenAnswer { invocation ->
        val runnable: Runnable = invocation.getArgument(0)
        val delay: Long = invocation.getArgument(1)

        mainThread.schedule(runnable, delay, TimeUnit.MILLISECONDS)

        null
    }
    return mockHandler
}