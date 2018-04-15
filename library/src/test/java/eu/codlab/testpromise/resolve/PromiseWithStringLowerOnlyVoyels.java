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

public class PromiseWithStringLowerOnlyVoyels {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] equals = {false};

        new Promise<String>(new PromiseSolver<String>() {
            @Override
            public void onCall(@NonNull Solver<String> solver) {
                solver.resolve("TintInTotoTutu");
            }
        })
                .then(new PromiseExec<String, String>() {
                    @Override
                    public void onCall(@Nullable String result, @NonNull Solver<String> solver) {
                        solver.resolve(result.toLowerCase());
                    }
                })
                .then(new PromiseExec<String, String>() {
                    @Override
                    public void onCall(@Nullable String result, @NonNull Solver<String> solver) {
                        solver.resolve(result.replaceAll("[^aeiouy]", ""));
                    }
                })
                .then(new PromiseExec<String, Void>() {
                    @Override
                    public void onCall(@Nullable String result, @NonNull Solver<Void> solver) {
                        System.out.println("result " + result);

                        equals[0] = "iioouu".equals(result);
                        latch.countDown();
                    }
                })
                .execute();



        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (!equals[0]) {
            throw new IllegalStateException("Expected same result...");
        }
    }
}
