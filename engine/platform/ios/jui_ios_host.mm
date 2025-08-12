#import "jui_ios_host.h"

jui_engine_t* jui_ios_attach_layer(CAMetalLayer* layer, int width, int height) {
    (void)layer; // TODO: use layer when Metal backend is implemented
    jui_engine_config_t cfg;
    cfg.width = width;
    cfg.height = height;
    cfg.device_type = 0; // metal
    cfg.native_surface = (__bridge void*)layer;
    return jui_engine_create(&cfg);
}

void jui_ios_tick(jui_engine_t* eng, double time_nanos) {
    jui_begin_frame(eng, time_nanos);
}


