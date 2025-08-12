package io.github._3xhaust.core.demo;

import examples.hello_world.Main;
import io.github._3xhaust.core.EngineBridge;
import io.github._3xhaust.core.IRRenderer;
import io.github._3xhaust.core.EngineRunner;
import io.github._3xhaust.core.View;

import java.nio.ByteBuffer;

public final class EngineDemo {
    public static void main(String[] args) {
        // Engine native library path can be provided via -Djui.engine.lib
        if (!EngineBridge.isNativeAvailable()) {
            System.err.println("Native engine library not loaded. Set -Djui.engine.lib to built lib path.");
        }

        View app = Main.MyApp();
        IRRenderer ir = new IRRenderer();
        ir.init("MyApp", 640, 480);
        ir.mount(app);
        ByteBuffer patch = ir.buildPatch();

        if (EngineBridge.isNativeAvailable()) {
            try (EngineRunner runner = new EngineRunner(640, 480, 3, 0L)) {
                runner.applyPatch(patch);
                runner.runFrames(1, 60);
            }
        }
        System.out.println("EngineDemo finished (stub). Patch size=" + (patch != null ? patch.remaining() : 0));
    }
}


