# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

file(GLOB NATIVE_SRC
    "tidy-html5/src/*.c"
    "src/main/cpp/*.c"
    "src/main/cpp/*.cpp")

####### c / c++ flags
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -D_REENTRANT -std=c++11 -Dulong='unsigned long int'")
# FIXME: _GNU_SOURCE is a temporial workaround for https://github.com/htacg/tidy-html5/issues/636
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -D_REENTRANT -std=c11 -D_GNU_SOURCE -Dulong='unsigned long int'")

add_library(tidyj SHARED
             ${NATIVE_SRC})

target_include_directories(tidyj PRIVATE
    "tidy-html5/include"
    "tidy-html5/src"
    )
