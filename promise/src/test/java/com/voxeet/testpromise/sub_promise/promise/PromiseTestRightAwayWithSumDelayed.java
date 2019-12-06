package com.voxeet.testpromise.sub_promise.promise;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.promise.solve.PromiseSolver;
import com.voxeet.promise.solve.Solver;
import com.voxeet.testpromise.utils.AndroidMockUtil;

/**
 * Created by kevinleperf on 06/04/2018.
 */

public class PromiseTestRightAwayWithSumDelayed {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final int[] final_result = {0};

        final Promise<Integer> resolve_later = new Promise<>(new PromiseSolver<Integer>() {
            @Override
            public void onCall(@NonNull final Solver<Integer> solver) {
                new Thread() {
                    @Override
                    public void run() {
                        System.out.println("sleeping...");
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        solver.resolve(10 * 10);
                    }
                }.run();
            }
        });

        System.out.println("executing test");
        new Promise<>(new PromiseSolver<Integer>() {
            @Override
            public void onCall(@NonNull Solver<Integer> solver) {
                solver.resolve(resolve_later
                        .then(new PromiseExec<Integer, Integer>() {
                            @Override
                            public void onCall(@Nullable Integer new_result, @NonNull Solver<Integer> solver) {
                                solver.resolve(new_result + 10);
                            }
                        }));
            }
        }).then(new PromiseExec<Integer, Object>() {
            @Override
            public void onCall(@Nullable Integer result, @NonNull Solver<Object> solver) {
                final_result[0] = result;
            }
        }).error(new ErrorPromise() {
            @Override
            public void onError(@NonNull Throwable error) {
                System.out.println("error catched");
                final_result[0] = 0;
                error.printStackTrace();
                latch.countDown();
            }
        });

        //6s are enough
        latch.await(4, TimeUnit.SECONDS);
        if (final_result[0] != 110) {
            throw new IllegalStateException("Expected 110... having " + final_result[0]);
        } else {
            System.out.println("having result " + final_result[0]);
        }
    }
}
