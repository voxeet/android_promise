package com.voxeet.promise;

public class Configuration {
    /**
     * Indicates if the solvers trying to resolve or reject something more than once should also
     * display such
     */
    public static OnMultipleSolverResolution onOnMultipleSolverResolution = null;

    /**
     * Indicate if the Promise should enable or disable multiple resolve / reject.
     * The default value is true so that it can continue to be compatible with previous implementations.
     * Future version of it will make that false by default & make it impossible to be changed
     */
    @Deprecated
    public static boolean enableMultipleResolveReject = true;

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
