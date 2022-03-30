package com.voxeet.promise.solve;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voxeet.promise.Promise;
import com.voxeet.promise.PromiseInOut;

/**
 * Simple solver to give ability to manage resolve/reject a/-synchronously
 */
public abstract class Solver<TYPE> {
    protected Solver() {

    }

    /**
     * Make the call resolve
     *
     * @param result a nullable result to propagate
     */
    public abstract void resolve(@Nullable TYPE result);

    public abstract <FIRST> void resolve(@NonNull PromiseInOut<FIRST, TYPE> promise);

    public abstract void resolve(@NonNull Promise<TYPE> promise);

    /**
     * Reject the current branch
     *
     * @param error the error to propagate
     */
    public abstract void reject(@NonNull Throwable error);
}
