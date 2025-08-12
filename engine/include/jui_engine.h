#pragma once

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct jui_engine jui_engine_t;

typedef struct {
    int32_t width;
    int32_t height;
    int32_t device_type;      // 0:metal,1:gles,2:d3d11,3:gl
    void* native_surface;     // CAMetalLayer*, ANativeWindow*, HWND/ID3DDevice*, GLFWwindow*, etc
} jui_engine_config_t;

// lifecycle
jui_engine_t* jui_engine_create(const jui_engine_config_t* cfg);
void jui_engine_destroy(jui_engine_t* eng);

// frame & patches
void jui_apply_patches(jui_engine_t* eng, const void* data, uint32_t size);
void jui_begin_frame(jui_engine_t* eng, double time_nanos);

// input
void jui_pointer_event(jui_engine_t* eng, int action, float x, float y, int buttons);
void jui_key_event(jui_engine_t* eng, int keycode, int down, int mods);

#ifdef __cplusplus
} // extern "C"
#endif


