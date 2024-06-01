cmake_minimum_required(VERSION 2.8.12)

project(dlib_android)
add_subdirectory(./dlib dlib_build)

add_library(
    dlib_utils
    SHARED
    dlib_utils.hpp
)

target_link_libraries(dlib_utils dlib::dlib)
