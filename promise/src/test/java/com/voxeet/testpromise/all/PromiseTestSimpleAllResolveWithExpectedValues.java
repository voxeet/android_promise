package com.voxeet.testpromise.all;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.promise.solve.PromiseSolver;
import com.voxeet.promise.solve.Solver;
import com.voxeet.testpromise.utils.AndroidMockUtil;

public class PromiseTestSimpleAllResolveWithExpectedValues {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        final boolean[] catched = new boolean[]{false};
        final boolean[] expected = new boolean[]{
                true, false, true, true
        };

        System.out.println("executing test");

        Promise.all(new Promise<>(new PromiseSolver<Boolean>() {
            @Override
            public void onCall(@NonNull Solver<Boolean> solver) {
                solver.resolve(true);
            }
        }), new Promise<Boolean>(new PromiseSolver<Boolean>() {
            @Override
            public void onCall(@NonNull Solver<Boolean> solver) {
                solver.resolve(false);
            }
        }), new Promise<>(new PromiseSolver<Boolean>() {
            @Override
            public void onCall(@NonNull Solver<Boolean> solver) {
                solver.resolve(true);
            }
        }), new Promise<>(new PromiseSolver<Boolean>() {
            @Override
            public void onCall(@NonNull Solver<Boolean> solver) {
                solver.resolve(true);
            }
        }))
                .then(new PromiseExec<List<Boolean>, Void>() {
                    @Override
                    public void onCall(@Nullable List<Boolean> result, @NonNull Solver<Void> solver) {
                        if (result.size() != expected.length) {
                            solver.reject(new IllegalStateException("Invalid result size"));
                        } else {
                            int index = 0;
                            for (; index < result.size(); index++) {
                                if (result.get(index) != expected[index]) {
                                    solver.reject(new IllegalStateException("Invalid result value at index " + index));
                                    latch.countDown();
                                    return;
                                }
                            }
                        }
                        latch.countDown();
                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        error.printStackTrace();
                        catched[0] = true;
                        latch.countDown();
                    }
                });

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (catched[0]) {
            throw new IllegalStateException("Expected 0 error...");
        }
    }
}
