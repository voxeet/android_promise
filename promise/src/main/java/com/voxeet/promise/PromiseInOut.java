package com.voxeet.promise;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.promise.solve.Solver;
import com.voxeet.promise.solve.ThenCallable;
import com.voxeet.promise.solve.ThenPromise;
import com.voxeet.promise.solve.ThenValue;
import com.voxeet.promise.solve.ThenVoid;

/**
 * Promise's logic management
 * Take a type "in" to create a type "out"
 */

public class PromiseInOut<TYPE, TYPE_RESULT> extends AbstractPromise<TYPE_RESULT> {
    @Nullable
    private Promise<TYPE_RESULT> mPromise;

    @Nullable
    private PromiseExec<TYPE, TYPE_RESULT> mSimiliPromise;
    private ErrorPromise mSimiliError;
    private PromiseInOut<Object, TYPE> mPromiseInOutParent;
    private PromiseInOut<TYPE_RESULT, Object> mPromiseInOutChild;
    private TYPE mResult;

    private PromiseInOut() {

    }

    PromiseInOut(@NonNull PromiseExec<TYPE, TYPE_RESULT> simili_promise) {
        this();
        mSimiliPromise = simili_promise;
    }

    PromiseInOut(@NonNull ErrorPromise simili_promise) {
        this();
        mSimiliError = simili_promise;
    }

    PromiseInOut(@NonNull Promise<TYPE_RESULT> promise) {
        this();
        mPromise = promise;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Public management
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public <TYPE_RESULT1> PromiseInOut<TYPE_RESULT, TYPE_RESULT1> then(Promise<TYPE_RESULT1> to_resolve) {
        return then(super.then(to_resolve));
    }

    public <EXPECTED_TYPE> PromiseInOut<TYPE_RESULT, EXPECTED_TYPE>
    then(PromiseExec<TYPE_RESULT, EXPECTED_TYPE> next_simili_promise) {
        return then(new PromiseInOut<>(next_simili_promise));
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * BLOCK OF js like
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public <EXPECTED_TYPE> PromiseInOut<TYPE_RESULT, EXPECTED_TYPE>
    then(final ThenValue<TYPE_RESULT, EXPECTED_TYPE> likeValue) {
        return then((result, solver) -> solver.resolve(likeValue.call(result)));
    }

    public <EXPECTED_TYPE> PromiseInOut<TYPE_RESULT, EXPECTED_TYPE>
    then(final ThenPromise<TYPE_RESULT, EXPECTED_TYPE> likePromise) {
        return then((result, solver) -> {
            try {
                solver.resolve(likePromise.call(result));
            } catch (Exception e) {
                solver.reject(e);
            }
        });
    }

    public <EXPECTED_TYPE> PromiseInOut<TYPE_RESULT, EXPECTED_TYPE>
    then(final ThenCallable<TYPE_RESULT, EXPECTED_TYPE> likeCallable) {
        return then((result, solver) -> {
            try {
                solver.resolve(likeCallable.call(result).call());
            } catch (Exception e) {
                solver.reject(e);
            }
        });
    }

    public <EXPECTED_TYPE> PromiseInOut<TYPE_RESULT, EXPECTED_TYPE>
    then(final ThenVoid<TYPE_RESULT> likeCallable) {
        return then((result, solver) -> {
            try {
                likeCallable.call(result);
            } catch (Exception e) {
                solver.reject(e);
            }
        });
    }

    public void error(ErrorPromise error) {
        mSimiliError = error;

        execute();
    }

    public void execute() {
        //top -> down
        PromiseDebug.log("PromiseInOut", "executing promise ----------");
        PromiseDebug.log("PromiseInOut", "having inout parent := " + mPromiseInOutParent);
        PromiseDebug.log("PromiseInOut", "having inout parent := " + mPromise);
        PromiseDebug.log("PromiseInOut", "executing promise ----------");
        if (null != mPromiseInOutParent) {
            mPromiseInOutParent.execute();
        } else if (mPromise != null) {
            Promise.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mPromise.resolve();
                }
            });
        }
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Package management
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void setParent(PromiseInOut<Object, TYPE> parent) {
        mPromiseInOutParent = parent;
    }

    void setChild(PromiseInOut<TYPE_RESULT, Object> child) {
        mPromiseInOutChild = child;
    }

    void execute(final Promise promise) {
        Promise.getHandler().post(() -> {
            if (promise == mPromise) {
                postAfterOnResult();
            }
        });
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Private management
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private <EXPECTED_TYPE> PromiseInOut<TYPE_RESULT, EXPECTED_TYPE>
    then(PromiseInOut<TYPE_RESULT, EXPECTED_TYPE> inout) {
        inout.setParent((PromiseInOut<Object, TYPE_RESULT>) this);
        this.setChild((PromiseInOut<TYPE_RESULT, Object>) inout);

        return inout;
    }


    private void setResult(TYPE result) {
        mResult = result;

        postAfterOnResult();
    }

    private void postAfterOnError(final Throwable error) {
        if (mSimiliError != null) {
            mSimiliError.onError(error);
        } else if (mPromiseInOutChild != null) {
            Promise.getHandler().post(() -> mPromiseInOutChild.postAfterOnError(error));
        }
    }

    private void postAfterOnResult() {
        if (mSimiliPromise != null) {
            Promise.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mSimiliPromise.onCall(mResult, new Solver<>() {
                            @Override
                            public <FIRST> void resolve(@NonNull PromiseInOut<FIRST, TYPE_RESULT> promise) {
                                promise.then((result, solver) -> postResult(result)).error(this::reject);
                            }

                            @Override
                            public void resolve(@NonNull Promise<TYPE_RESULT> promise) {
                                promise.then((result, solver) -> resolve(result)).error(this::reject);

                            }

                            @Override
                            public void resolve(@Nullable TYPE_RESULT result) {
                                if (result instanceof Promise) {
                                    resolve((Promise) result);
                                } else {
                                    postResult(result);
                                }
                            }

                            @Override
                            public void reject(@NonNull Throwable error) {
                                postAfterOnError(error);
                            }
                        });
                    } catch (Exception error) {
                        postAfterOnError(error);
                    }
                }
            });
        } else if (mPromise != null) {
            Promise.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mPromise.getSolver().onCall(new Solver<>() {
                            @Override
                            public void resolve(@Nullable TYPE_RESULT result) {
                                postResult(result);
                            }

                            @Override
                            public <FIRST> void resolve(@NonNull PromiseInOut<FIRST, TYPE_RESULT> promise) {
                                promise.then((result, solver) -> resolve(result)).error(this::reject);
                            }

                            @Override
                            public void resolve(@NonNull Promise<TYPE_RESULT> promise) {
                                promise.then((result, solver) -> resolve(result)).error(this::reject);
                            }

                            @Override
                            public void reject(@NonNull Throwable error) {
                                postAfterOnError(error);
                            }
                        });
                    } catch (Exception e) {
                        postAfterOnError(e);
                    }
                }
            });
        }
    }

    private void postResult(final TYPE_RESULT result) {
        Promise.getHandler().post(() -> {
            if (mPromiseInOutChild != null)
                mPromiseInOutChild.setResult(result);
        });
    }

}
