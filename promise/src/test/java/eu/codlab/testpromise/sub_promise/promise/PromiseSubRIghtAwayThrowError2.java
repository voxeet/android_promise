package eu.codlab.testpromise.sub_promise.promise;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import eu.codlab.simplepromise.Promise;
import eu.codlab.simplepromise.PromiseInOut;
import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.PromiseSolver;
import eu.codlab.simplepromise.solve.Solver;
import eu.codlab.testpromise.utils.AndroidMockUtil;

/**
 * Created by kevinleperf on 06/04/2018.
 */

public class PromiseSubRIghtAwayThrowError2 {

    @Before
    public void setHandler() {
        Promise.setHandler(AndroidMockUtil.mockMainThreadHandler());
    }

    @Test
    public void test() throws InterruptedException {

        final IllegalStateException[] mException = {null};
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] equals = {false};

        final PromiseInOut<Integer, Integer> resolve_later = new Promise<>(new PromiseSolver<Integer>() {
            @Override
            public void onCall(@NonNull final Solver<Integer> solver) {
                solver.resolve(10);
            }
        }).then(new PromiseExec<Integer, Integer>() {
            @Override
            public void onCall(@Nullable Integer result, @NonNull Solver<Integer> solver) {
                try {
                    throw new IllegalStateException();
                } catch (IllegalStateException e) {
                    mException[0] = e;
                    solver.reject(e);
                }
            }
        });

        System.out.println("executing test");
        new Promise<>(new PromiseSolver<Integer>() {
            @Override
            public void onCall(@NonNull Solver<Integer> solver) {
                solver.resolve(resolve_later);
            }
        }).error(new ErrorPromise() {
            @Override
            public void onError(@NonNull Throwable error) {
                System.out.println("error catched");
                equals[0] = mException[0] == error;
                error.printStackTrace();
                latch.countDown();
            }
        });

        //6s are enough
        latch.await(6, TimeUnit.SECONDS);
        if (equals[0] != true) {
            throw new IllegalStateException("Expected exception same...");
        }
    }
}
