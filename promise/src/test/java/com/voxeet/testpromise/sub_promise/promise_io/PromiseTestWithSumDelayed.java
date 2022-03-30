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

public class PromiseTestWithSumDelayed {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final int[] final_result = {0};

        final PromiseInOut<Integer, Integer> resolve_later = new Promise<>((PromiseSolver<Integer>) solver -> new Thread() {
            @Override
            public void run() {
                System.out.println("sleeping...");
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                solver.resolve(10);
            }
        }.run()).then((result, solver) -> solver.resolve(result * 10));

        System.out.println("executing test");
        execute()
                .then((PromiseExec<Integer, Integer>) (result, solver) -> solver.resolve(resolve_later
                        .then((new_result, solver1) -> solver1.resolve(new_result + result))))
                .then((result, solver) -> final_result[0] = result)
                .error(error -> {
                    System.out.println("error catched");
                    final_result[0] = 0;
                    error.printStackTrace();
                    latch.countDown();
                });

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (final_result[0] != 110) {
            throw new IllegalStateException("Expected 110... having " + final_result[0]);
        } else {
            System.out.println("having result " + final_result[0]);
        }
    }

    private Promise<Integer> execute() {
        return new Promise<>(solver -> solver.resolve(10));
    }
}
