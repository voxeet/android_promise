package com.voxeet.promise.solve;

import java.util.concurrent.Callable;

/**
 * Interface to use return a Callable implementation which will get called
 * @param <ENTRY>
 * @param <TYPE>
 */
public interface ThenCallable<ENTRY, TYPE> extends PromiseLikeGeneric<ENTRY, Callable<TYPE>> {
}
