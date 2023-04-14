package com.voxeet.promise

import com.voxeet.promise.solve.ThenVoid
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@SuppressWarnings("TooGenericExceptionCaught", "PrintStackTrace")
@Deprecated("This method will exist until the Configuration lets developers use resolve/reject multiple")
fun <T> CancellableContinuation<T>.safeResumeWithException(e: Throwable) {
    try {
        this.resumeWithException(e)
    } catch (e: Throwable) {
        println("Warning, in future versions, this will lead to unexpected crash.")
        e.printStackTrace()
    }
}

suspend fun <T> Promise<T>.await(): T = suspendCancellableCoroutine { continuation ->
    this.then(ThenVoid { continuation.resume(it) })
        .error { continuation.safeResumeWithException(it) }
}

suspend fun <T> Promise<T>.awaitNullable(): T? = suspendCancellableCoroutine { continuation ->
    this.then(ThenVoid { continuation.resume(it) })
        .error { continuation.safeResumeWithException(it) }
}

suspend fun <T : Any> Promise<T>.awaitNonNull(): T =
    suspendCancellableCoroutine { continuation ->
        this.then {
            if (null == it) throw NullPointerException("Promise result : value was null")
            continuation.resume(it)
        }.error { continuation.safeResumeWithException(it) }
    }

suspend fun <I, O> PromiseInOut<I, O>.await(): O =
    suspendCancellableCoroutine { continuation ->
        this.then { continuation.resume(it) }
            .error { continuation.safeResumeWithException(it) }
    }

suspend fun <I, O> PromiseInOut<I, O>.awaitNullable(): O? =
    suspendCancellableCoroutine { continuation ->
        this.then { continuation.resume(it) }
            .error { continuation.safeResumeWithException(it) }
    }

suspend fun <I, O : Any> PromiseInOut<I, O>.awaitNonNull(): O =
    suspendCancellableCoroutine { continuation ->
        this.then {
            if (null == it) throw NullPointerException("Promise result : value was null")
            continuation.resume(it)
        }.error { continuation.safeResumeWithException(it) }
    }
