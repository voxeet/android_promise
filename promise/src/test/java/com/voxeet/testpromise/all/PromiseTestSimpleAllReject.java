package com.voxeet.testpromise.all;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.testpromise.utils.AndroidMockUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PromiseTestSimpleAllReject {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        final boolean[] catched = {true};
        System.out.println("executing test");

        Promise.all(new Promise<>(solver -> {
                    try {
                        throw new NullPointerException("oops");
                    } catch (Exception e) {
                        solver.reject(e);
                    }
                }), Promise.resolve("called 2")
        ).then((PromiseExec<List<String>, Void>) (result, solver) -> {
            System.out.println(Arrays.toString(result.toArray()));
            catched[0] = false;
            latch.countDown();
        }).error(error -> {
            error.printStackTrace();
            catched[0] = true;
            latch.countDown();
        });

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (!catched[0]) {
            throw new IllegalStateException("Expected an error...");
        }
    }
}
