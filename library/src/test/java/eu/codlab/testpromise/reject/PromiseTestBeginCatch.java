package eu.codlab.testpromise.reject;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import eu.codlab.simplepromise.ErrorPromise;
import eu.codlab.simplepromise.Promise;
import eu.codlab.simplepromise.PromiseSolver;
import eu.codlab.simplepromise.Solver;
import eu.codlab.testpromise.utils.AndroidMockUtil;

public class PromiseTestBeginCatch {

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
                try {
                    throw new IllegalAccessException("Yup");
                } catch (IllegalAccessException e) {
                    solver.reject(e);
                }
                System.out.println("you should see this :p");
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
