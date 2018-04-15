package eu.codlab.simplepromise.solve;

import android.support.annotation.NonNull;

/**
 * Class used as a main entry point for the Promise
 */

public abstract class PromiseSolver<TYPE> {
    /**
     * Call resolve or reject in this method implementation
     * <p>
     * Warning : if your call is async, the final result will
     * be posted into the promise handler's looper
     */
    public abstract void onCall(@NonNull Solver<TYPE> solver);

}
