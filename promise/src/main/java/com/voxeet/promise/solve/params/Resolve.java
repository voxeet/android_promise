package com.voxeet.promise.solve.params;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.Solver;

public class Resolve<TYPE> {

    @NonNull
    private final Solver<TYPE> solver;

    public Resolve(@NonNull Solver<TYPE> solver) {
        this.solver = solver;
    }

    public void call(@Nullable TYPE value) {
        //due to exception encountered in some jvm, check for type explicitly as well
        if (value instanceof Promise) {
            solver.resolve((Promise<TYPE>) value);
        } else {
            solver.resolve(value);
        }
    }

    public void call(@NonNull Promise promise) {
        solver.resolve(promise);
    }
}
