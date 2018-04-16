package eu.codlab.testpromise.all;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import eu.codlab.simplepromise.Promise;
import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.PromiseSolver;
import eu.codlab.simplepromise.solve.Solver;
import eu.codlab.testpromise.utils.AndroidMockUtil;

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

        Promise.all(new Promise<>(new PromiseSolver<String>() {
            @Override
            public void onCall(@NonNull Solver<String> solver) {
                try {
                    throw new NullPointerException("oops");
                } catch (Exception e) {
                    solver.reject(e);
                }
            }
        }), new Promise<>(new PromiseSolver<String>() {
            @Override
            public void onCall(@NonNull Solver<String> solver) {
                solver.resolve("called 2");
            }
        }))
                .then(new PromiseExec<List<String>, Void>() {
                    @Override
                    public void onCall(@Nullable List<String> result, @NonNull Solver<Void> solver) {
                        System.out.println(Arrays.toString(result.toArray()));
                        catched[0] = false;
                        latch.countDown();
                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        error.printStackTrace();
                        catched[0] = true;
                        latch.countDown();
                    }
                });

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (!catched[0]) {
            throw new IllegalStateException("Expected an error...");
        }
    }
}
