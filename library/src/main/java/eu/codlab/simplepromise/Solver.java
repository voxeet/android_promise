package eu.codlab.simplepromise;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Simple solver to give ability to manage resolve/reject a/-synchronously
 */
public abstract class Solver<TYPE> {
    Solver() {

    }

    /**
     * Make the call resolve
     *
     * @param result a nullable result to propagate
     */
    public abstract void resolve(@Nullable TYPE result);

    /**
     * Reject the current branch
     *
     * @param error the error to propagate
     */
    public abstract void reject(@NonNull Throwable error);
}
