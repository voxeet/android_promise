package com.voxeet.promise;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.promise.solve.Solver;

public abstract class AbstractPromise<TYPE_EXECUTE> {

    public abstract <TYPE_RESULT> PromiseInOut<TYPE_EXECUTE, TYPE_RESULT>
    then(PromiseExec<TYPE_EXECUTE, TYPE_RESULT> to_resolve);

    /**
     * Let possible to resolve a promise in chain
     *
     * This notation will discard the result from the previous
     *
     * @param to_resolve the promise to make the new chain
     * @param <TYPE_RESULT> the type of result from the promise
     *
     * @return an object resolvable
     */
    public <TYPE_RESULT> PromiseInOut<TYPE_EXECUTE, TYPE_RESULT>
    then(final Promise<TYPE_RESULT> to_resolve) {
        return new PromiseInOut<>(new PromiseExec<TYPE_EXECUTE, TYPE_RESULT>() {
            @Override
            public void onCall(@Nullable TYPE_EXECUTE result, @NonNull Solver<TYPE_RESULT> solver) {
                solver.resolve(to_resolve);
            }
        });
    }
}
