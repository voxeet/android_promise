package com.voxeet.promise;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HandlerFactory {

    @Nullable
    private static Handler handler;

    public static void setHandler(@NonNull Handler handler) {
        HandlerFactory.handler = handler;
    }

    private HandlerFactory() {

    }

    @NonNull
    public static Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

}
