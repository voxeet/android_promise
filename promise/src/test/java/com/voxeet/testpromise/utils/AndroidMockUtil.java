package com.voxeet.testpromise.utils;

import android.os.Handler;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * From gist https://gist.github.com/dpmedeiros/7f7724fdf13fc5390bb05958448cdcad
 */
public class AndroidMockUtil {

    private AndroidMockUtil() {
    }

    /**
     * Mocks main thread handler post() and postDelayed() for use in Android unit tests
     * <p>
     * To use this:
     * <ol>
     * <li>Call this method in an {@literal @}Before method of your test.</li>
     * <li>Place Looper.class in the {@literal @}PrepareForTest annotation before your test class.</li>
     * <li>any class under test that needs to call {@code new Handler(Looper.getMainLooper())} should be placed
     * in the {@literal @}PrepareForTest annotation as well.</li>
     * </ol>
     */
    public static Handler mockMainThreadHandler() {
        Handler mockMainThreadHandler = mock(Handler.class);
        Answer<Boolean> handlerPostAnswer = new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Runnable runnable = invocation.getArgument(0);
                Long delay = 0L;
                if (invocation.getArguments().length > 1) {
                    delay = invocation.getArgument(1);
                }
                if (runnable != null) {
                    mainThread.schedule(runnable, delay, TimeUnit.MILLISECONDS);
                }
                return true;
            }
        };
        doAnswer(handlerPostAnswer).when(mockMainThreadHandler).post(any(Runnable.class));
        doAnswer(handlerPostAnswer).when(mockMainThreadHandler).postDelayed(any(Runnable.class), anyLong());

        return mockMainThreadHandler;
    }

    private final static ScheduledExecutorService mainThread = Executors.newSingleThreadScheduledExecutor();

}