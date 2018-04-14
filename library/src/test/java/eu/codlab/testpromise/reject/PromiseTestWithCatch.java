package eu.codlab.testpromise.reject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import eu.codlab.simplepromise.ErrorPromise;
import eu.codlab.simplepromise.Promise;
import eu.codlab.simplepromise.PromiseExec;
import eu.codlab.simplepromise.PromiseSolver;
import eu.codlab.simplepromise.Solver;
import eu.codlab.testpromise.utils.AndroidMockUtil;

public class PromiseTestWithCatch {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        final boolean[] catched = {false};
        System.out.println("executing test");
        new Promise<String>(new PromiseSolver<String>() {
            @Override
            public void onCall(@NonNull Solver<String> solver) {
                System.out.println("onCall");
                solver.resolve("result!!");
            }
        })
                .then(new PromiseExec<String, Integer>() {
                    @Override
                    public void onCall(@Nullable String result, @NonNull Solver<Integer> solver) {
                        solver.reject(new IllegalStateException());
                    }
                })
                .then(new PromiseExec<Integer, Void>() {
                    @Override
                    public void onCall(@Nullable Integer result, @NonNull Solver<Void> solver) {
                        System.out.println("should not be seen");
                        latch.countDown();
                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        System.out.println("error catched");
                        catched[0] = true;
                        error.printStackTrace();
                        latch.countDown();
                    }
                });

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (!catched[0]) {
            throw new IllegalStateException("Expected error...");
        }
    }
}
