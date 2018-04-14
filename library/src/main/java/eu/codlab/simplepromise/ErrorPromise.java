package eu.codlab.simplepromise;


import android.support.annotation.NonNull;

public abstract class ErrorPromise {

    /**
     * Manage the error propagated from the promise
     *
     * @param error a non null error
     */
    public abstract void onError(@NonNull Throwable error);
}
