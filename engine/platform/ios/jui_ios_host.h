#pragma once

#ifdef __OBJC__
#import <Foundation/Foundation.h>
#import <QuartzCore/CAMetalLayer.h>
#endif

#include <stdint.h>
#include "../../include/jui_engine.h"

#ifdef __cplusplus
extern "C" {
#endif

// Attach engine to a CAMetalLayer-backed view. Returns engine pointer.
jui_engine_t* jui_ios_attach_layer(CAMetalLayer* layer, int width, int height);

// Advance one frame (CADisplayLink callback)
void jui_ios_tick(jui_engine_t* eng, double time_nanos);

#ifdef __cplusplus
}
#endif


