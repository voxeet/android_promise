package com.voxeet.promise;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.promise.solve.PromiseSolver;
import com.voxeet.promise.solve.Solver;

public class Promise<TYPE> extends AbstractPromise<TYPE> {

    public static <T> Promise<List<T>> all(AbstractPromise<T>... promises) {
        return new PromiseAll<T>(promises).all();
    }

    @NonNull
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void setHandler(@NonNull Handler handler) {
        sHandler = handler;
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

    @Override
    public <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT> then(Promise<TYPE_RESULT> to_resolve) {
        return then(super.then(to_resolve));
    }

    private <TYPE_RESULT> PromiseInOut<TYPE, TYPE_RESULT> then(PromiseInOut<TYPE, TYPE_RESULT> created_inout) {
        PromiseDebug.log("Promise", "then PromiseInOut " + created_inout);
        created_inout.setParent(mPromiseInOut);
        mPromiseInOut.setChild((PromiseInOut<TYPE, Object>) created_inout);

        return created_inout;
    }
}
