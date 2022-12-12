package com.voxeet.promise

import com.voxeet.promise.solve.ThenVoid
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Promise<T>.await(): T? = suspendCancellableCoroutine { continuation ->
    this
        .then(ThenVoid { continuation.resume(it) })
        .error { continuation.resumeWithException(it) }
}

suspend fun <T> Promise<T>.awaitNonNull(): T = suspendCancellableCoroutine { continuation ->
    this
        .then(ThenVoid { continuation.resume(it) })
        .error { continuation.resumeWithException(it) }
}

suspend fun <I, O> PromiseInOut<I, O>.await(): O? = suspendCancellableCoroutine { continuation ->
    this.then { continuation.resume(it) }
        .error { continuation.resumeWithException(it) }
}

suspend fun <I, O> PromiseInOut<I, O>.awaitNonNull(): O =
    suspendCancellableCoroutine { continuation ->
        this.then { continuation.resume(it) }
            .error { continuation.resumeWithException(it) }
    }
