package eu.codlab.simplepromise;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

public class PromiseDebug {

    private static boolean DEBUG_ACTIVATED = false;

    private PromiseDebug() {

    }

    public static void activate(boolean state) {
        DEBUG_ACTIVATED = state;
    }

    public static void log(@NonNull String tag, @NonNull String line) {
        if (DEBUG_ACTIVATED && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(line)) {
            Log.d(tag, line);
        }
    }
}
