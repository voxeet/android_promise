package com.voxeet.testpromise.errors;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.testpromise.utils.AndroidMockUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PromiseTestNPECatch2 {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        final boolean[] catched = {false};
        System.out.println("executing test");
        new Promise<String>(solver -> solver.resolve(((String) null).toLowerCase()))
                .then((PromiseExec<String, Void>) (result, solver) -> {
                    System.out.println("you should not see this");
                    catched[0] = false;
                    latch.countDown();
                }).error(error -> {
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
