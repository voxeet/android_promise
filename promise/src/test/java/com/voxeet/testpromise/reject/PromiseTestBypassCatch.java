package com.voxeet.testpromise.reject;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.testpromise.utils.AndroidMockUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PromiseTestBypassCatch {

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
            try {
                throw new IllegalAccessException("Yup");
            } catch (IllegalAccessException e) {
                solver.reject(e);
            }
            System.out.println("you should see this :p");
        }).then((PromiseExec<String, String>) (result, solver) -> solver.resolve("but not this ^^"))
                .then((result) -> {
                    System.out.println(result);
                    latch.countDown();
                })
                .error((ErrorPromise) error -> {
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
