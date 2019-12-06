package com.voxeet.promise.solve;

import com.voxeet.promise.Promise;

public interface ThenPromise<ENTRY, TYPE> extends PromiseLikeGeneric<ENTRY, Promise<TYPE>> {
}
