package com.voxeet.promise;

import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.promise.solve.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kevinleperf on 15/04/2018.
 */

class PromiseAll<TYPE_EXECUTE> extends AbstractPromiseMulti<TYPE_EXECUTE> {

    private AtomicInteger mDone = new AtomicInteger(0);
    private AtomicBoolean mSent = new AtomicBoolean(false);
    private boolean mIsRejected = false;
    private Solver<List<TYPE_EXECUTE>> mSolver;
    private Object[] mResults;

    public PromiseAll(AbstractPromise<TYPE_EXECUTE>... promises) {
        super(promises);
    }

    public PromiseAll(List<AbstractPromise<TYPE_EXECUTE>> promises) {
        super(promises);
    }

    public Promise<List<TYPE_EXECUTE>> all() {
        return new Promise<>(final_solver -> {
            mResults = new Object[getPromises().size()];
            mSolver = final_solver;
            int index = 0;

            for (AbstractPromise<TYPE_EXECUTE> promise : getPromises()) {
                final int current_index = index++;
                promise.then((PromiseExec<TYPE_EXECUTE, Void>) (result, solver) -> {
                    System.out.println("executing for " + current_index);
                    if (!mIsRejected) {
                        mResults[current_index] = result;
                        mDone.incrementAndGet();

                        if (!mSent.get() && mDone.get() == getPromises().size()) {
                            mSent.set(true);
                            onDone();
                        }
                    }
                }).error(error -> {
                    if (!mSent.get() && !mIsRejected) {
                        mSent.set(true);
                        mIsRejected = true;
                        mSolver.reject(error);
                    }
                });
            }
        });
    }

    private void onDone() {
        List<TYPE_EXECUTE> list = new ArrayList<>();
        for (Object object : mResults) list.add((TYPE_EXECUTE) object);
        mSolver.resolve(list);
    }
}
