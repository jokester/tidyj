cmake_minimum_required(VERSION 2.6)

project(tidyj C CXX)

# set(CMAKE_BUILD_TYPE Release)
set(CMAKE_VERBOSE_MAKEFILE OFF)

include(UseJava)
find_package(JNI REQUIRED)

file(GLOB NATIVE_SRC
    "tidy-html5/src/*.c"
    "src/main/native/*.c"
    "src/main/native/*.cpp")

####### c / c++ flags
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -D_REENTRANT -std=c++11")
# FIXME: _GNU_SOURCE is a temporial workaround for https://github.com/htacg/tidy-html5/issues/636
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -D_REENTRANT -std=c11 -D_GNU_SOURCE")

add_library(tidyj SHARED ${NATIVE_SRC})

target_include_directories(tidyj PRIVATE
    "tidy-html5/include"
    "tidy-html5/src"
    ${JAVA_INCLUDE_PATH}
    ${JAVA_INCLUDE_PATH2})

set_target_properties(tidyj
    PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/jniLib"
)
