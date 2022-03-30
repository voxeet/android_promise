package com.voxeet.testpromise.resolve;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.PromiseSolver;
import com.voxeet.testpromise.utils.AndroidMockUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PromiseTestSimpleResolve {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        final boolean[] catched = {true};
        System.out.println("executing test");
        new Promise<>((PromiseSolver<String>) solver -> {
            catched[0] = false;
            solver.resolve("called");
            latch.countDown();
        }).execute();

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (catched[0]) {
            throw new IllegalStateException("Expected 0 error...");
        }
    }
}
