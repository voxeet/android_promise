package com.voxeet.testpromise.sub_promise.promise_io;

import com.voxeet.promise.Promise;
import com.voxeet.promise.PromiseInOut;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.promise.solve.PromiseSolver;
import com.voxeet.testpromise.utils.AndroidMockUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by kevinleperf on 06/04/2018.
 */

public class PromiseSubThrowError {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {

        final IllegalStateException[] mException = {null};
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] equals = {false};

        final PromiseInOut<Integer, Integer> resolve_later = new Promise<>((PromiseSolver<Integer>) solver -> {
            try {
                throw new IllegalStateException();
            } catch (IllegalStateException e) {
                mException[0] = e;
                solver.reject(e);
            }
        }).then((result, solver) -> {
            mException[0] = null;
            solver.resolve(Integer.MAX_VALUE);
        });

        System.out.println("executing test");
        execute().then((PromiseExec<Integer, Integer>) (result, solver) -> solver.resolve(resolve_later))
                .error(error -> {
                    System.out.println("error catched");
                    equals[0] = mException[0] == error;
                    error.printStackTrace();
                    latch.countDown();
                });

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (equals[0] != true) {
            throw new IllegalStateException("Expected exception same...");
        }
    }

    private Promise<Integer> execute() {
        return new Promise<>(solver -> solver.resolve(10));
    }
}
