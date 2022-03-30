package com.voxeet.promise.solve;

import androidx.annotation.NonNull;

public interface ErrorPromise {

    /**
     * Manage the error propagated from the promise
     *
     * @param error a non null error
     */
    void onError(@NonNull Throwable error);
}
