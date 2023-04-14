package com.voxeet.testpromise

import com.voxeet.promise.Configuration

fun registerConfigurationForNoRethrowResolve() {
    Configuration.onOnMultipleSolverResolution = Configuration.OnMultipleSolverResolution{ obj, exc ->
        exc.printStackTrace()
        throw java.lang.IllegalStateException("It wasn't expected to have $obj resolved/thrown")
    }
}