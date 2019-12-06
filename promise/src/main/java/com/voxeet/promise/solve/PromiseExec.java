package com.voxeet.promise.solve;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Simple interface which will give success and error methods
 */

public interface PromiseExec<TYPE, RESULT> {

    /**
     * Success method, called from Promise's then()
     *
     * @param result a valid or nullable result from the previous resolution
     * @param solver a solver which resolve or reject method must be called
     */
    void onCall(@Nullable TYPE result, @NonNull Solver<RESULT> solver);

}
