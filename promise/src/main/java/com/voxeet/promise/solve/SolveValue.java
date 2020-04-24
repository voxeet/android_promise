package com.voxeet.promise.solve;

import android.support.annotation.NonNull;

/**
 * Class used as a main entry point for the Promise
 */

public interface SolveValue<TYPE> {
    @NonNull
    TYPE onCall();
}
