package io.github._3xhaust.core;

import io.github._3xhaust.platform.swing.SwingRenderer;

import javax.swing.*;

public class App {
    public static void run(View view) {
        SwingRenderer renderer = new SwingRenderer();
        renderer.init("MyApp", 640, 480);
        SwingUtilities.invokeLater(() -> renderer.mount(view));
    }

    public static void run(String title, View view) {
        SwingRenderer renderer = new SwingRenderer();
        renderer.init(title, 640, 480);
        SwingUtilities.invokeLater(() -> renderer.mount(view));
    }

    public static void run(String title, int width, int height, View view) {
        SwingRenderer renderer = new SwingRenderer();
        renderer.init(title, width, height);
        SwingUtilities.invokeLater(() -> renderer.mount(view));
    }
}
