package com.voxeet.testpromise.resolve;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.PromiseExec;
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

        System.out.println("executing test");
        execute().then((PromiseExec<Integer, Integer>) (result, solver) -> new Thread() {
            @Override
            public void run() {
                System.out.println("sleeping...");
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                solver.resolve(10 + result);
            }
        }.start()).then(result -> {
            final_result[0] = 10 + result;
            latch.countDown();
        }).error(error -> {
            System.out.println("error catched");
            final_result[0] = 0;
            error.printStackTrace();
            latch.countDown();
        });

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (final_result[0] != 30) {
            throw new IllegalStateException("Expected 30... having " + final_result[0]);
        } else {
            System.out.println("having result " + final_result[0]);
        }
    }

    private Promise<Integer> execute() {
        return new Promise<>(solver -> solver.resolve(10));
    }
}
