#pragma once

#include <android/native_window.h>
#include <stdint.h>
#include "../../include/jui_engine.h"

#ifdef __cplusplus
extern "C" {
#endif

// Attach engine to an Android ANativeWindow. Returns engine pointer.
jui_engine_t* jui_android_attach_window(ANativeWindow* win, int width, int height);

// Advance one frame (Choreographer callback)
void jui_android_tick(jui_engine_t* eng, double time_nanos);

#ifdef __cplusplus
}
#endif


