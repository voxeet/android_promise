package com.voxeet.testpromise.resolve;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.testpromise.utils.AndroidMockUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PromiseTestWithSum {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final int[] final_result = {0};

        System.out.println("executing test");
        execute()
                .then((PromiseExec<Integer, Integer>) (result, solver) -> solver.resolve(10 + result))
                .then((PromiseExec<Integer, Integer>) (result, solver) -> {
                    final_result[0] = 10 + result;
                    latch.countDown();
                })
                .error(error -> {
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


    @Test
    public void testSoloResolve() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final int[] final_result = {0};

        System.out.println("executing test");
        execute().then((PromiseExec<Integer, Integer>) (result, solver) -> {
            System.out.println("---------");
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
        if (final_result[0] != 20) {
            throw new IllegalStateException("Expected 20... having " + final_result[0]);
        } else {
            System.out.println("having result " + final_result[0]);
        }
    }

    private Promise<Integer> execute() {
        return new Promise<>(solver -> solver.resolve(10));
    }
}
