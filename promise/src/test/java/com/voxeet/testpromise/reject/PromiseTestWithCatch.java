package com.voxeet.testpromise.reject;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.testpromise.utils.AndroidMockUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PromiseTestWithCatch {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        final boolean[] catched = {false};
        System.out.println("executing test");
        new Promise<String>(solver -> {
            System.out.println("onCall");
            solver.resolve("result!!");
        })
                .then((PromiseExec<String, Integer>) (result, solver) -> solver.reject(new IllegalStateException()))
                .then((PromiseExec<Integer, Void>) (result, solver) -> {
                    System.out.println("should not be seen");
                    latch.countDown();
                })
                .error(error -> {
                    System.out.println("error catched");
                    catched[0] = true;
                    error.printStackTrace();
                    latch.countDown();
                });

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (!catched[0]) {
            throw new IllegalStateException("Expected error...");
        }
    }
}
