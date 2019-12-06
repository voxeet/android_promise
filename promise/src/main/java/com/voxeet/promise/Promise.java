package com.voxeet.promise;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.promise.solve.PromiseSolver;
import com.voxeet.promise.solve.Solver;
import com.voxeet.promise.solve.ThenCallable;
import com.voxeet.promise.solve.ThenPromise;
import com.voxeet.promise.solve.ThenValue;
import com.voxeet.promise.solve.ThenVoid;

public class Promise<TYPE> extends AbstractPromise<TYPE> {

    @Nullable
    private static ExecutorService sExecutorService;

    public static <T> Promise<List<T>> all(AbstractPromise<T>... promises) {
        return new PromiseAll<T>(promises).all();
    }

    @NonNull
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void setHandler(@NonNull Handler handler) {
        sHandler = handler;
    }

    public static void setExecutorService(@NonNull ExecutorService executorService) {
        sExecutorService = executorService;
    }

    @NonNull
    public static Handler getHandler() {
        return sHandler;
    }

    private PromiseSolver<TYPE> mSolver;
    private PromiseInOut<Object, TYPE> mPromiseInOut;

    private Promise() {

    }

    public Promise(PromiseSolver<TYPE> solver) {
        this();
        PromiseDebug.activate(true);
        mSolver = solver;
        mPromiseInOut = new PromiseInOut<>(this);
    }

    private Promise(TYPE value) {
        this(new PromiseSolver<TYPE>() {
            @Override
            public void onCall(@NonNull Solver<TYPE> solver) {
                solver.resolve(value);
            }
        });
    }

    public static <TYPE> Promise<TYPE> resolve(TYPE value) {
        return new Promise<>(value);
    }

    public static <TYPE> Promise<TYPE> reject(@NonNull Exception to_throw) {
        try {
            throw to_throw;
        } catch (Exception e) {
            return new Promise<>(new PromiseSolver<TYPE>() {
                @Override
                public void onCall(@NonNull Solver<TYPE> solver) {
                    solver.reject(e);
                }
            });
        }
    }

    @Override
    public <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT> then(PromiseExec<TYPE, TYPE_RESULT> to_resolve) {
        PromiseDebug.log("Promise", "then PromiseExec");
        return then(new PromiseInOut<>(to_resolve));
    }

    public <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT> then(final TYPE_RESULT resolved) {
        return then(new PromiseExec<TYPE, TYPE_RESULT>() {
            @Override
            public void onCall(@Nullable TYPE result, @NonNull Solver<TYPE_RESULT> solver) {
                solver.resolve(resolved);
            }
        });
    }

    @Override
    public <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT> then(Promise<TYPE_RESULT> to_resolve) {
        return then(super.then(to_resolve));
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * BLOCK OF js like
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT>
    then(final ThenValue<TYPE, TYPE_RESULT> likeValue) {
        return then(new PromiseExec<TYPE, TYPE_RESULT>() {
            @Override
            public void onCall(@Nullable TYPE result, @NonNull Solver<TYPE_RESULT> solver) {
                solver.resolve(likeValue.call(result));
            }
        });
    }

    public <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT>
    then(final ThenPromise<TYPE, TYPE_RESULT> likePromise) {
        return then(new PromiseExec<TYPE, TYPE_RESULT>() {
            @Override
            public void onCall(@Nullable TYPE result, @NonNull Solver<TYPE_RESULT> solver) {
                try {
                    solver.resolve(likePromise.call(result));
                } catch (Exception e) {
                    solver.reject(e);
                }
            }
        });
    }

    public <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT>
    then(final ThenCallable<TYPE, TYPE_RESULT> likeCallable) {
        return then(new PromiseExec<TYPE, TYPE_RESULT>() {
            @Override
            public void onCall(@Nullable TYPE result, @NonNull Solver<TYPE_RESULT> solver) {
                try {
                    if (null != sExecutorService) {
                        sExecutorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    solver.resolve(likeCallable.call(result).call());
                                } catch (Exception e) {
                                    solver.reject(e);
                                }
                            }
                        });
                    } else {
                        throw new IllegalStateException("No Executor service, please use Promise.setExecutorService(...)");
                    }
                } catch (Exception e) {
                    solver.reject(e);
                }
            }
        });
    }

    public PromiseInOut<TYPE, Void>
    then(final ThenVoid<TYPE> likeCallable) {
        return then(new PromiseExec<TYPE, Void>() {
            @Override
            public void onCall(@Nullable TYPE result, @NonNull Solver<Void> solver) {
                try {
                    likeCallable.call(result);
                    solver.resolve((Void) null);
                } catch (Exception e) {
                    solver.reject(e);
                }
            }
        });
    }

    public <TYPE_RESULT> void error(ErrorPromise to_error) {
        PromiseDebug.log("Promise", "then error");
        PromiseInOut<TYPE, TYPE_RESULT> promise_inout = new PromiseInOut<>(to_error);
        promise_inout.setParent(mPromiseInOut);
        mPromiseInOut.setChild((PromiseInOut<TYPE, Object>) promise_inout);

        promise_inout.execute();
    }

    public void execute() {
        PromiseDebug.log("Promise", "execute " + mPromiseInOut);
        mPromiseInOut.execute();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Package management
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void resolve() {
        PromiseDebug.log("Promise", "resolve " + mPromiseInOut);
        mPromiseInOut.execute(this);
    }

    PromiseSolver<TYPE> getSolver() {
        return mSolver;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Private management
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    private <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT> then(PromiseInOut<TYPE, TYPE_RESULT> created_inout) {
        PromiseDebug.log("Promise", "then PromiseInOut " + created_inout);
        created_inout.setParent(mPromiseInOut);
        mPromiseInOut.setChild((PromiseInOut<TYPE, Object>) created_inout);

        return created_inout;
    }
}
