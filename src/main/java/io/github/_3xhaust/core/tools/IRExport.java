package io.github._3xhaust.core.tools;

import examples.hello_world.Main;
import io.github._3xhaust.core.IRRenderer;
import io.github._3xhaust.core.View;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public final class IRExport {
    public static void main(String[] args) throws Exception {
        String out = args.length > 0 ? args[0] : "build/ir/app.ir";
        View app = Main.MyApp();
        IRRenderer ir = new IRRenderer();
        ir.mount(app);
        ByteBuffer buf = ir.buildPatch();
        byte[] bytes = new byte[buf.remaining()];
        buf.get(bytes);
        try (FileOutputStream fos = new FileOutputStream(out)) {
            fos.write(bytes);
        }
        System.out.println("IR exported: " + out + " (" + bytes.length + " bytes)");
    }
}


