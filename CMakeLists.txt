cmake_minimum_required(VERSION 3.20)
project(jui_engine C)

set(CMAKE_POSITION_INDEPENDENT_CODE ON)

add_library(jui_engine SHARED
    engine/src/jui_engine_stub.c
    engine/src/jni/engine_jni.c
)

target_include_directories(jui_engine PUBLIC engine/include)

find_package(JNI REQUIRED)
target_include_directories(jui_engine PRIVATE ${JNI_INCLUDE_DIRS})
target_link_libraries(jui_engine PRIVATE ${JNI_LIBRARIES})

# Placeholders for platform hosts (headers only for now)
target_include_directories(jui_engine PUBLIC engine/platform/ios engine/platform/android)

if(APPLE)
    set_target_properties(jui_engine PROPERTIES OUTPUT_NAME "jui_engine")
elseif(WIN32)
    set_target_properties(jui_engine PROPERTIES OUTPUT_NAME "jui_engine")
else()
    set_target_properties(jui_engine PROPERTIES OUTPUT_NAME "jui_engine")
endif()


