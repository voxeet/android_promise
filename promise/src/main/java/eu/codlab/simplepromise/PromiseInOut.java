package eu.codlab.simplepromise;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;

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

    public <EXPECTED_TYPE> PromiseInOut<TYPE_RESULT, EXPECTED_TYPE>
    then(PromiseExec<TYPE_RESULT, EXPECTED_TYPE> next_simili_promise) {
        return then(new PromiseInOut<>(next_simili_promise));
    }

    public void error(ErrorPromise error) {
        mSimiliError = error;

        execute();
    }

    public void execute() {
        //top -> down
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
        Promise.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (promise == mPromise) {
                    postAfterOnResult();
                }
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
            Promise.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mPromiseInOutChild.postAfterOnError(error);
                }
            });
        }
    }

    private void postAfterOnResult() {
        if (mSimiliPromise != null) {
            Promise.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mSimiliPromise.onCall(mResult, new Solver<TYPE_RESULT>() {
                            @Override
                            public void resolve(@Nullable TYPE_RESULT result) {
                                postResult(result);
                            }

                            @Override
                            public <FIRST> void resolve(@NonNull PromiseInOut<FIRST, TYPE_RESULT> promise) {
                                promise
                                        .then(new PromiseExec<TYPE_RESULT, Object>() {
                                            @Override
                                            public void onCall(@Nullable TYPE_RESULT result, @NonNull Solver<Object> solver) {
                                                postResult(result);
                                            }
                                        })
                                        .error(new ErrorPromise() {
                                            @Override
                                            public void onError(@NonNull Throwable error) {
                                                reject(error);
                                            }
                                        });
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
                        mPromise.getSolver().onCall(new Solver<TYPE_RESULT>() {
                            @Override
                            public void resolve(@Nullable TYPE_RESULT result) {
                                postResult(result);
                            }

                            @Override
                            public <FIRST> void resolve(@NonNull PromiseInOut<FIRST, TYPE_RESULT> promise) {
                                promise
                                        .then(new PromiseExec<TYPE_RESULT, Object>() {
                                            @Override
                                            public void onCall(@Nullable TYPE_RESULT result, @NonNull Solver<Object> solver) {
                                                postResult(result);
                                            }
                                        })
                                        .error(new ErrorPromise() {
                                            @Override
                                            public void onError(@NonNull Throwable error) {
                                                reject(error);
                                            }
                                        });
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
        Promise.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mPromiseInOutChild != null)
                    mPromiseInOutChild.setResult(result);
            }
        });
    }

}
