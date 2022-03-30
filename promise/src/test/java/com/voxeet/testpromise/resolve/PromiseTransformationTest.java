package com.voxeet.testpromise.resolve;

import static junit.framework.Assert.assertEquals;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.testpromise.utils.AndroidMockUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PromiseTransformationTest {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void addition_isCorrect() throws Exception {
        final int[] test_result = {0};
        final CountDownLatch latch = new CountDownLatch(1);

        Promise<Boolean> promise = Promise.resolve(false);
        promise.then((PromiseExec<Boolean, String>) (result, solver) -> solver.resolve("test2"))
                .then((PromiseExec<String, Integer>) (result, solver) -> solver.resolve(result.length()))
                .then(result -> {
                    test_result[0] = result;

                    latch.countDown();
                }).execute();

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        assertEquals(5, test_result[0]);

    }
}