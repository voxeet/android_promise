package com.voxeet.promise.solve;

import androidx.annotation.NonNull;

import com.voxeet.promise.Promise;

/**
 * Class used as a main entry point for the Promise
 */
public interface SolvePromise<TYPE> {
    @NonNull
    Promise<TYPE> onCall();
}
