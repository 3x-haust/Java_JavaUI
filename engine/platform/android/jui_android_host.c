#include "jui_android_host.h"

jui_engine_t* jui_android_attach_window(ANativeWindow* win, int width, int height) {
    (void)win; // TODO: use native window when GLES backend is implemented
    jui_engine_config_t cfg;
    cfg.width = width;
    cfg.height = height;
    cfg.device_type = 1; // gles
    cfg.native_surface = (void*)win;
    return jui_engine_create(&cfg);
}

void jui_android_tick(jui_engine_t* eng, double time_nanos) {
    jui_begin_frame(eng, time_nanos);
}


