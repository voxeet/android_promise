package com.voxeet.promise.solve;

public interface PromiseLikeGeneric<ENTRY, T> {

    T call(ENTRY entry) throws Throwable;
}
