package com.voxeet.testpromise.sub_promise.promise;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.PromiseSolver;
import com.voxeet.testpromise.utils.AndroidMockUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by kevinleperf on 06/04/2018.
 */

public class PromiseTestWithSumDelayed2 {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final int[] final_result = {0};

        System.out.println("executing test");
        new Promise<>((PromiseSolver<Integer>) solver -> solver.resolve(new Promise<>(solver1 -> new Thread() {
            @Override
            public void run() {
                System.out.println("sleeping...");
                try {
                    sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                solver1.resolve(10 * 10);
            }
        }.start())))
                .then((result, solver) -> final_result[0] = result)
                .error(error -> {
                    System.out.println("error catched");
                    final_result[0] = 0;
                    error.printStackTrace();
                    latch.countDown();
                });

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (final_result[0] != 100) {
            throw new IllegalStateException("Expected 100... having " + final_result[0]);
        } else {
            System.out.println("having result " + final_result[0]);
        }
    }
}
