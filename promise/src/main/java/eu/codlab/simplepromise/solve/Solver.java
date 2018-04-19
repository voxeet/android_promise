package eu.codlab.simplepromise.solve;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import eu.codlab.simplepromise.Promise;
import eu.codlab.simplepromise.PromiseInOut;

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
