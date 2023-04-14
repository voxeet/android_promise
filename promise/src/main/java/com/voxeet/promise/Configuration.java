package com.voxeet.promise;

public class Configuration {
    /**
     * Indicates if the solvers trying to resolve or reject something more than once should also
     * display such
     */
    public static OnMultipleSolverResolution onOnMultipleSolverResolution = null;

    public interface OnMultipleSolverResolution {
        /**
         * Method called whenever a value is tried to be sent or rejected after another one already was.
         *
         * @param value       the erroneous call's value
         * @param actualStack the stack from which it came from
         */
        void onWarning(Object value, Exception actualStack);
    }
}
