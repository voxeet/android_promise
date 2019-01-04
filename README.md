# Simple Promise for Android

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

[![Travis](https://travis-ci.org/codlab/android_promise.svg?branch=master)](https://travis-ci.org/codlab/android_promise)

## Import

In your build.gradle file, add the following :

```
repositories {
    maven {
        url  "https://dl.bintray.com/codlab/oss"
    }
}
```



## Usage

The minimalistic Promise can be write such as :

```java
    new Promise<>(new PromiseSolver<String>() {
        @Override
        public void onCall(@NonNull Solver<String> solver) {
            //TODO implement your logic here
        }
    }).execute();
```

You can chain the promise with the following sample:

```java
    Promise<Boolean> promise = new Promise<>(new PromiseSolver<Boolean>() {
        @Override
        public void onCall(@NonNull Solver<Boolean> solver) {
            solver.resolve(false);
        }
    });

    //chain it
    promise
            .then(new PromiseExec<Boolean, String>() {
                @Override
                public void onCall(@Nullable Boolean result, Solver<String> solver) {
                    solver.resolve("previous result is := " + result);
                }
            })
            .then(new PromiseExec<String, Integer>() {
                @Override
                public void onCall(@Nullable String result, Solver<Integer> solver) {
                    solver.resolve(result.length());
                }
            })
            .then(new PromiseExec<Integer, Object>() {
                @Override
                public void onCall(@Nullable Integer result, Solver<Object> solver) {
                    //TODO something with the result which is the length of the appended false
                }
            })
            .execute();
```

You can do whatever you want in the promise or the chained execution (Thread, Async post, Bugs, Save a cat)

## Resolve/Reject results calls

The Promises are using post to an Handler to manage the `resolve()` / `reject()` calls made to the `Solver`.

You can change the overall Handler using `Promise.setHandler(yourNonNullHandler)`

## execute (resolve) the Promise

Resolving a promise is as easy as calling `execute()` or manage `error(<ErrorPromise>)`

Once one of those 2 calls are made, the promise will start resolving itself.

Note that it is the best practice to manage the execution flow using `error(<ErrorPromise>)` to be sure
to grab any issues the Promise resolution could throw !

those following methods exists to resolve :

- resolve(Promise)

  post a new promise to resolve

- resolve(PromiseInOut)

  post a promise chained to resolve

- resolve(value)

  post a value as a ... result to the next

Example :
```
    new Promise<String>(new PromiseSolver<String>() {
            @Override
            public void onCall(@NonNull Solver<String> solver) {
                solver.resolve(null);
            }
        })
        .then(new PromiseExec<String, String>() {
            @Override
            public void onCall(@Nullable String result, @NonNull Solver<String> solver) {
                //exception thrown right here
                solver.resolve(result.toLowerCase());
            }
        })
        .then(new PromiseExec<String, Void>() {
            @Override
            public void onCall(@Nullable String result, @NonNull Solver<Void> solver) {
                System.out.println("you should not see this");
            }
        })
        .error(new ErrorPromise() {
            @Override
            public void onError(@NonNull Throwable error) {
                System.out.println("error catched");
                error.printStackTrace();
            }
        });
```

Note : error() calls execute
## Contributing

When contributing to this repository, please first discuss the change you wish to make via issue,
email, or any other method with the owners of this repository before making a change.

## License

This project is licensed under the LGPL v3 License - see the [LICENSE](LICENSE) file for details
