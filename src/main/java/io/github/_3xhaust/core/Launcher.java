package io.github._3xhaust.core;

import io.github._3xhaust.platform.javafx.FXBootstrap;
import io.github._3xhaust.platform.swing.SwingRenderer;
import javafx.application.Application;

import java.util.Locale;
import java.util.function.Supplier;

public final class Launcher {
    private Launcher() {}

    public enum OsPlatform { IOS, ANDROID, MACOS, WINDOWS, LINUX, UNKNOWN }
    public enum RendererType { SWING, JAVAFX }

    public static void launch(String[] args, Supplier<View> appFactory) {
        launch(args, appFactory, "MyApp", 640, 480);
    }

    public static void launch(String[] args, Supplier<View> appFactory, String title, int width, int height) {
        OsPlatform osPlatform = detectOsPlatform();
        RendererType renderer = selectRenderer(osPlatform);
        switch (renderer) {
            case JAVAFX -> {
                FXBootstrap.configure(appFactory, title, width, height);
                Application.launch(FXBootstrap.class, args);
            }
            case SWING -> {
                // OS 네임스페이스 렌더러로 추상화 (현재는 Swing 공용 구현)
                App.run(new SwingRenderer(), title, width, height, appFactory.get());
            }
        }
    }

    private static OsPlatform detectOsPlatform() {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (osName.contains("ios")) return OsPlatform.IOS;
        if (osName.contains("mac")) return OsPlatform.MACOS;
        if (osName.contains("win")) return OsPlatform.WINDOWS;
        if (osName.contains("nux") || osName.contains("nix") || osName.contains("linux")) return OsPlatform.LINUX;
        // ANDROID는 런타임 특성으로 감지
        String vmName = System.getProperty("java.vm.name", "").toLowerCase(Locale.ROOT);
        if (vmName.contains("dalvik") || System.getProperty("java.runtime.name", "").toLowerCase(Locale.ROOT).contains("android")) {
            return OsPlatform.ANDROID;
        }
        return OsPlatform.UNKNOWN;
    }

    private static RendererType selectRenderer(OsPlatform osPlatform) {
        // 모바일은 JavaFX (Gluon)
        if (osPlatform == OsPlatform.IOS || osPlatform == OsPlatform.ANDROID) {
            return RendererType.JAVAFX;
        }
        // Desktop 기본은 Swing, override 가능
        String fromProp = System.getProperty("javaui.renderer");
        if (fromProp != null && !fromProp.isBlank()) {
            return parseRenderer(fromProp);
        }
        String fromEnv = System.getenv("JAVAUI_RENDERER");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return parseRenderer(fromEnv);
        }
        return RendererType.SWING;
    }

    private static RendererType parseRenderer(String token) {
        if (token == null) return RendererType.SWING;
        String t = token.trim().toLowerCase(Locale.ROOT);
        return switch (t) {
            case "javafx", "fx" -> RendererType.JAVAFX;
            case "swing" -> RendererType.SWING;
            default -> RendererType.SWING;
        };
    }
}


