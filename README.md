# tidyj

A Java JNI wrapper for [libtidy](http://www.html-tidy.org/developer/).

[![CircleCI](https://circleci.com/gh/jokester/tidyj.svg?style=svg)](https://circleci.com/gh/jokester/tidyj)

## Features

Current: v0.2

- basic HTML / XML tiding, and not breaking things
- Android support and an example app

Next: v0.3
- Control over memory usage

<!--
- v0.4 Readonly DOM access
- v0.3
 -->
<!-- TODO: add example app -->

## For Users

### How to use from Java (gradle)

Currently one need to copy code (`lib/src/main`), CMake configuration (`lib/CMakeLists.txt`),
and a few gradle tasks (in `lib/build.gradle`) to use from another Java project.

// I am not sure how should one "publish" native code for traditional Java,
or how do Java developers consume them. <!-- TODO: update -->
(If you are familiar of this, creating a issue is more than welcome.)

### How to use from Android

Currently one need to copy related files to Android module. See `android-demo/` for an example.

<!-- TODO: publish aar for android? -->

### Threading

All public APIs of `tidyj` are thread-safe.

`libtidy` cares little about thread and concurrency stuff:
all code just run on caller thread, and do nothing after return. Not all its APIs are thread safe.
We handle that in `tidyj`.

### Memory

Currently (before v0.3), underlying `tidylib` uses `free / memory`
to allocate memory in completely non-managed [native heap](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/memleaks005.html#sthref46).

<!-- TODO: add a close() method -->

<!-- TODO:
milestone v0.3: memory management
- provide

Each `TidyHTML5` instance creates a direct `ByteBuffer` for `libtidy` to use.

JVM GC knows how to free the native heap behind a direct ByteBuffer.
However a direct buffer have a small memory footpoint in Java heap, and may live longer than necessary before GC.

In case this concerns you, call `#free()` to free . Consequent calls on a throw
If this concerns you: a user can
NOTE:
-->

## For developers

### How to build

To build the native code, `CMake`, `make` and configured C/C++ (both) compilers are required.

```sh
# after clone, fetch libtidy as dependcies
$ git submodule init
$ git submodule update

# `test` task will run cmake and make as needed
# If test runs, the build should be fine.
$ ./gradlew lib:test
```

## License

MIT

## How to contribute

I am open to help of any kind. If you find something interesting (can | need) to be be done, feel free to create a issue.

