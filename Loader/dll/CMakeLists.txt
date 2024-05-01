cmake_minimum_required(VERSION 3.28)
project(dll C)

set(CMAKE_C_STANDARD 11)

include_directories(.)

add_library(
        injection
        SHARED
        src/dll/ReflectiveLoader.c
        src/dll/ReflectiveLoader.h
        src/dll/Main.c
        src/win.h
)

add_library(
        api
        SHARED
        src/inject/GetProcAddressR.c
        src/inject/Inject.c
        src/inject/LoadLibraryR.c
        src/win.h
        src/inject/cn_yapeteam_injector_Utils.h
        src/dll/Main.h
)
