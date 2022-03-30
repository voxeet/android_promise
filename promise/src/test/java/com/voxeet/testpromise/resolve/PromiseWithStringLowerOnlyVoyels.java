package com.voxeet.testpromise.resolve;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.testpromise.utils.AndroidMockUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PromiseWithStringLowerOnlyVoyels {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] equals = {false};

        new Promise<String>(solver -> solver.resolve("TintInTotoTutu"))
                .then((PromiseExec<String, String>) (result, solver) -> solver.resolve(result.toLowerCase()))
                .then((PromiseExec<String, String>) (result, solver) -> solver.resolve(result.replaceAll("[^aeiouy]", "")))
                .then(result -> {
                    System.out.println("result " + result);

                    equals[0] = "iioouu".equals(result);
                    latch.countDown();
                })
                .execute();


        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (!equals[0]) {
            throw new IllegalStateException("Expected same result...");
        }
    }
}
