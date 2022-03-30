package com.voxeet.promise;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.promise.solve.PromiseSolver;
import com.voxeet.promise.solve.ResolveReject;
import com.voxeet.promise.solve.SolvePromise;
import com.voxeet.promise.solve.SolveValue;
import com.voxeet.promise.solve.Solver;
import com.voxeet.promise.solve.ThenCallable;
import com.voxeet.promise.solve.ThenPromise;
import com.voxeet.promise.solve.ThenValue;
import com.voxeet.promise.solve.ThenVoid;
import com.voxeet.promise.solve.params.Reject;
import com.voxeet.promise.solve.params.Resolve;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class Promise<TYPE> extends AbstractPromise<TYPE> {

    @Nullable
    private static ExecutorService sExecutorService;

    public static <T> Promise<List<T>> all(AbstractPromise<T>... promises) {
        return new PromiseAll<T>(promises).all();
    }

    public static <T> Promise<List<T>> all(List<AbstractPromise<T>> promises) {
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
        mSolver = solver;
        mPromiseInOut = new PromiseInOut<>(this);
    }

    public Promise(SolvePromise<TYPE> value) {
        this(solver -> solver.resolve(value.onCall()));
    }

    public Promise(@NonNull ResolveReject<TYPE> resolveReject) {
        this();
        mSolver = solver -> resolveReject.onCall(new Resolve<TYPE>(solver),
                new Reject(solver));
        mPromiseInOut = new PromiseInOut<>(this);
    }

    public <TYPE_IN> Promise(@NonNull PromiseInOut<TYPE_IN, TYPE> toResolve) {
        this(solver -> toResolve.then((ThenVoid<TYPE>) solver::resolve)
                .error(solver::reject));
    }

    private Promise(final TYPE value) {
        this(solver -> solver.resolve(value));
    }

    public static <TYPE> Promise<TYPE> resolve(TYPE value) {
        return new Promise<>(value);
    }

    public static <TYPE> Promise<TYPE> reject(@NonNull Throwable to_throw) {
        try {
            throw to_throw;
        } catch (final Throwable e) {
            return new Promise<>(solver -> solver.reject(e));
        }
    }

    public static void reject(@NonNull Solver solver, @NonNull Throwable to_throw) {
        try {
            throw to_throw;
        } catch (final Throwable e) {
            solver.reject(e);
        }
    }

    @Override
    public <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT> then(PromiseExec<TYPE, TYPE_RESULT> to_resolve) {
        PromiseDebug.log("Promise", "then PromiseExec");
        return then(new PromiseInOut<>(to_resolve));
    }

    public <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT> then(final TYPE_RESULT resolved) {
        return then((result, solver) -> solver.resolve(resolved));
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
        return then((result, solver) -> solver.resolve(likeValue.call(result)));
    }

    public <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT>
    then(final ThenPromise<TYPE, TYPE_RESULT> likePromise) {
        return then((result, solver) -> {
            try {
                solver.resolve(likePromise.call(result));
            } catch (Exception e) {
                solver.reject(e);
            }
        });
    }

    public <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT>
    then(final ThenCallable<TYPE, TYPE_RESULT> likeCallable) {
        return then((result, solver) -> {
            try {
                if (null != sExecutorService) {
                    sExecutorService.execute(() -> {
                        try {
                            solver.resolve(likeCallable.call(result).call());
                        } catch (Exception e) {
                            solver.reject(e);
                        }
                    });
                } else {
                    throw new IllegalStateException("No Executor service, please use Promise.setExecutorService(...)");
                }
            } catch (Exception e) {
                solver.reject(e);
            }
        });
    }

    public PromiseInOut<TYPE, Void>
    then(final ThenVoid<TYPE> likeCallable) {
        return then((result, solver) -> {
            try {
                likeCallable.call(result);
                solver.resolve((Void) null);
            } catch (Exception e) {
                solver.reject(e);
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
