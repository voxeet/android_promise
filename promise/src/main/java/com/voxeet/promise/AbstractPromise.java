package com.voxeet.promise;

import com.voxeet.promise.solve.PromiseExec;

public abstract class AbstractPromise<TYPE_EXECUTE> {

    public abstract <TYPE_RESULT> PromiseInOut<TYPE_EXECUTE, TYPE_RESULT>
    then(PromiseExec<TYPE_EXECUTE, TYPE_RESULT> to_resolve);

    /**
     * Let possible to resolve a promise in chain
     * <p>
     * This notation will discard the result from the previous
     *
     * @param to_resolve    the promise to make the new chain
     * @param <TYPE_RESULT> the type of result from the promise
     * @return an object resolvable
     */
    public <TYPE_RESULT> PromiseInOut<TYPE_EXECUTE, TYPE_RESULT>
    then(final Promise<TYPE_RESULT> to_resolve) {
        return new PromiseInOut<>((result, solver) -> solver.resolve(to_resolve));
    }
}
