package com.voxeet.promise.solve.params;

import android.support.annotation.NonNull;

import com.voxeet.promise.solve.Solver;

public class Reject {

    @NonNull
    private Solver solver;

    public Reject(@NonNull Solver solver) {
        this.solver = solver;
    }

    public void call(@NonNull Throwable error) {
        solver.reject(error);
    }

}
