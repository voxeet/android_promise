# Promise for Android

[![License: Apache 2](https://img.shields.io/badge/License-Apache2-blue.svg)](./LICENSE.md)

[![Travis](https://travis-ci.org/voxeet/sdk-android-lib-promise.svg?branch=master)](https://travis-ci.org/voxeet/sdk-android-lib-promise)

## Import

In your build.gradle file, add the following :

```
implementation "io.dolby:promise:${version}"
implementation "io.dolby:promise-ktx:${version}"
```

## Usage

Refer to [original implementation](https://github.com/codab/android_promise) for documentation

## Future implementations

Currently it's possible to resolve or reject multiple time from the same Promise/block. However
to mimic a standard behaviour, this won't be possible in the future. To prevent any disruption in production,
the current behaviour is to accept those but future versions won't. You can test your implementation using
`Configuration.enableMultipleResolveReject = false`.

This will render the app to block such attempts.