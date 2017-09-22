# tidyj

---

A Java JNI wrapper for [libtidy](http://www.html-tidy.org/developer/).

## Status

Still in early development, not advised to use in serious product.

## Planned features

- v0.0 chaos (we are here)
- v0.1 basic HTML / XML tiding, and not breaking things
- v0.2 Readonly DOM access
- v0.3 Android support and example app
- v0.4 Control over memory usage

<!-- TODO: add example app -->

## For Users

### How to use from Java

Frankly speaking I am not sure how can one publish hybrid + Java code for traditional Java,
or how do Java developers consume them. <!-- TODO: update -->

If you are familiar of this, creating a issue is more than welcome.

### How to use from Android

Not done yet (goal of v0.3).

<!-- TODO: publish aar for android -->

### Threading

`tidyj` (and `libtidy` inside) cares little about thread switching:
all code just run on caller thread, and do nothing after return.

A consumer is expected to manage thread by themselves. We attempt to make Java API less thread-unsafe.

### Memory

Currently (before v0.3), underlying `tidylib` uses `free / memory`
to allocate memory in [native heap](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/memleaks005.html#sthref46).

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

