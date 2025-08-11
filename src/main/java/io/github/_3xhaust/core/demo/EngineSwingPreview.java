package io.github._3xhaust.core.demo;

import examples.hello_world.Main;
import io.github._3xhaust.core.EngineBridge;
import io.github._3xhaust.core.EngineRunner;
import io.github._3xhaust.core.IRRenderer;
import io.github._3xhaust.core.View;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class EngineSwingPreview {
    private static BufferedImage readPPM(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            StringBuilder header = new StringBuilder();
            int c;
            // Read magic
            while ((c = fis.read()) != -1 && c != '\n') header.append((char) c);
            if (!header.toString().trim().equals("P6")) throw new IOException("Not P6 PPM");
            // Read size
            StringBuilder dims = new StringBuilder();
            int w = -1, h = -1, maxv = 255;
            while (true) {
                StringBuilder line = new StringBuilder();
                while ((c = fis.read()) != -1 && c != '\n') line.append((char) c);
                String s = line.toString().trim();
                if (s.isEmpty() || s.startsWith("#")) continue;
                String[] parts = s.split(" ");
                if (parts.length >= 2) { w = Integer.parseInt(parts[0]); h = Integer.parseInt(parts[1]); break; }
            }
            // max value
            StringBuilder mv = new StringBuilder();
            while ((c = fis.read()) != -1 && c != '\n') mv.append((char) c);
            maxv = Integer.parseInt(mv.toString().trim());
            if (w <= 0 || h <= 0) throw new IOException("Invalid size");
            byte[] rgb = fis.readNBytes(w * h * 3);
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            int idx = 0;
            for (int y = 0; y < h; y++) {
                for (int x2 = 0; x2 < w; x2++) {
                    int r = rgb[idx++] & 0xFF;
                    int g = rgb[idx++] & 0xFF;
                    int b = rgb[idx++] & 0xFF;
                    int rgbInt = (r << 16) | (g << 8) | b;
                    img.setRGB(x2, y, rgbInt);
                }
            }
            return img;
        }
    }

    public static void main(String[] args) throws Exception {
        if (!EngineBridge.isNativeAvailable()) {
            System.err.println("Set -Djui.engine.lib to native lib path");
            return;
        }

        // Build IR
        View app = Main.MyApp();
        IRRenderer ir = new IRRenderer();
        ir.mount(app);
        ByteBuffer patch = ir.buildPatch();

        // Start engine frames on background thread
        EngineRunner runner = new EngineRunner(640, 480, 3, 0L);
        runner.applyPatch(patch);
        Thread t = new Thread(() -> runner.runFrames(600, 30), "engine-loop");
        t.setDaemon(true);
        t.start();

        // Swing window to preview PPM frames
        JFrame f = new JFrame("Engine Preview");
        JLabel lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.getContentPane().add(new JScrollPane(lbl), BorderLayout.CENTER);
        f.setSize(660, 520);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        Timer timer = new Timer(1000 / 30, e -> {
            try {
                File outDir = new File("build-native/out");
                File ppm = new File(outDir, "frame_0.ppm");
                if (ppm.exists()) {
                    BufferedImage img = readPPM(ppm);
                    lbl.setIcon(new ImageIcon(img));
                }
            } catch (Exception ex) {
                // ignore errors
            }
        });
        timer.start();
    }
}


