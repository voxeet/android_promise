package com.voxeet.testpromise.all;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.testpromise.utils.AndroidMockUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PromiseTestSimpleAllResolveWithExpectedValues {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        final boolean[] catched = new boolean[]{false};
        final boolean[] expected = new boolean[]{
                true, false, true, true
        };

        System.out.println("executing test");

        Promise.all(Promise.resolve(true),
                Promise.resolve(false),
                Promise.resolve(true),
                Promise.resolve(true)
        ).then((PromiseExec<List<Boolean>, Void>) (result, solver) -> {
            if (result.size() != expected.length) {
                solver.reject(new IllegalStateException("Invalid result size"));
            } else {
                int index = 0;
                for (; index < result.size(); index++) {
                    if (result.get(index) != expected[index]) {
                        solver.reject(new IllegalStateException("Invalid result value at index " + index));
                        latch.countDown();
                        return;
                    }
                }
            }
            latch.countDown();
        }).error(error -> {
            error.printStackTrace();
            catched[0] = true;
            latch.countDown();
        });

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (catched[0]) {
            throw new IllegalStateException("Expected 0 error...");
        }
    }
}
