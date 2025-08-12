#include <jni.h>
#include <stdint.h>
#include "../include/jui_engine.h"

JNIEXPORT jlong JNICALL Java_io_github__13xhaust_core_EngineBridge_nEngineCreate
  (JNIEnv* env, jclass cls, jint width, jint height, jint deviceType, jlong nativeSurfacePtr) {
    (void)env; (void)cls; (void)nativeSurfacePtr;
    jui_engine_config_t cfg = { width, height, deviceType, (void*)(uintptr_t)nativeSurfacePtr };
    jui_engine_t* eng = jui_engine_create(&cfg);
    return (jlong)(uintptr_t)eng;
}

JNIEXPORT void JNICALL Java_io_github__13xhaust_core_EngineBridge_nEngineDestroy
  (JNIEnv* env, jclass cls, jlong handle) {
    (void)env; (void)cls;
    jui_engine_destroy((jui_engine_t*)(uintptr_t)handle);
}

JNIEXPORT void JNICALL Java_io_github__13xhaust_core_EngineBridge_nEngineApplyPatches
  (JNIEnv* env, jclass cls, jlong handle, jobject byteBuffer, jint size) {
    (void)cls;
    void* data = (*env)->GetDirectBufferAddress(env, byteBuffer);
    if (data && size > 0) {
        jui_apply_patches((jui_engine_t*)(uintptr_t)handle, data, (uint32_t)size);
    }
}

JNIEXPORT void JNICALL Java_io_github__13xhaust_core_EngineBridge_nEngineBeginFrame
  (JNIEnv* env, jclass cls, jlong handle, jdouble timeNanos) {
    (void)env; (void)cls;
    jui_begin_frame((jui_engine_t*)(uintptr_t)handle, (double)timeNanos);
}

JNIEXPORT void JNICALL Java_io_github__13xhaust_core_EngineBridge_nEngineDispatchPointer
  (JNIEnv* env, jclass cls, jlong handle, jint action, jfloat x, jfloat y, jint buttons) {
    (void)env; (void)cls;
    jui_pointer_event((jui_engine_t*)(uintptr_t)handle, action, x, y, buttons);
}

JNIEXPORT void JNICALL Java_io_github__13xhaust_core_EngineBridge_nEngineDispatchKey
  (JNIEnv* env, jclass cls, jlong handle, jint keyCode, jint down, jint mods) {
    (void)env; (void)cls;
    jui_key_event((jui_engine_t*)(uintptr_t)handle, keyCode, down, mods);
}


