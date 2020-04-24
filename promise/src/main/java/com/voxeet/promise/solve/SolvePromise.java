package com.voxeet.promise.solve;

import android.support.annotation.NonNull;

import eu.codlab.simplepromise.Promise;

/**
 * Class used as a main entry point for the Promise
 */

public interface SolvePromise<TYPE> {
    @NonNull
    Promise<TYPE> onCall();
}
