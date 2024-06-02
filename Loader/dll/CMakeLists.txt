cmake_minimum_required(VERSION 3.28)
project(injection)

set(CMAKE_CXX_STANDARD 14)

include_directories(.)

add_library(
        injection
        SHARED
        src/dll/Native.c
)
