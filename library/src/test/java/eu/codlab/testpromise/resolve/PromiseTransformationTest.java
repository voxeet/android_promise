package eu.codlab.testpromise.resolve;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import eu.codlab.simplepromise.Promise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.PromiseSolver;
import eu.codlab.simplepromise.solve.Solver;
import eu.codlab.testpromise.utils.AndroidMockUtil;

import static junit.framework.Assert.assertEquals;

public class PromiseTransformationTest {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void addition_isCorrect() throws Exception {
        final int[] test_result = {0};
        final CountDownLatch latch = new CountDownLatch(1);

        Promise<Boolean> promise = new Promise<>(new PromiseSolver<Boolean>() {
            @Override
            public void onCall(@NonNull Solver<Boolean> solver) {
                solver.resolve(false);
            }
        });
        promise
                .then(new PromiseExec<Boolean, String>() {
                    @Override
                    public void onCall(@Nullable Boolean result, Solver<String> solver) {
                        solver.resolve("test2");
                    }
                })
                .then(new PromiseExec<String, Integer>() {
                    @Override
                    public void onCall(@Nullable String result, Solver<Integer> solver) {
                        solver.resolve(result.length());
                    }
                })
                .then(new PromiseExec<Integer, Object>() {
                    @Override
                    public void onCall(@Nullable Integer result, Solver<Object> solver) {
                        test_result[0] = result;

                        latch.countDown();
                    }
                })
                .execute();

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        assertEquals(5, test_result[0]);

    }
}