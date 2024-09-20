cmake_minimum_required(VERSION 3.25)
project(codeC)

set(CMAKE_CXX_STANDARD 17)

add_executable(codeC test.c cJSON.c)
