package com.voxeet.promise.solve;

import androidx.annotation.NonNull;

import com.voxeet.promise.solve.params.Reject;
import com.voxeet.promise.solve.params.Resolve;

public interface ResolveReject<TYPE> {

    void onCall(@NonNull Resolve<TYPE> resolve,
                @NonNull Reject reject) throws Throwable;

}
