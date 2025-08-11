package io.github._3xhaust.platform.javafx;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.platform.android.AndroidRenderer;
import io.github._3xhaust.platform.ios.IOSRenderer;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

public class FXBootstrap extends Application {
    private static Supplier<View> appFactory;
    private static String appTitle = "MyApp";
    private static int appWidth = 640;
    private static int appHeight = 480;

    // Explicit public no-arg constructor for GraalVM reflection
    public FXBootstrap() {
        super();
    }

    public static void configure(Supplier<View> factory, String title, int width, int height) {
        appFactory = Objects.requireNonNull(factory, "factory");
        appTitle = title != null ? title : appTitle;
        appWidth = width > 0 ? width : appWidth;
        appHeight = height > 0 ? height : appHeight;
    }

    @Override
    public void start(Stage stage) {
        Renderer renderer;
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (osName.contains("ios")) renderer = new IOSRenderer(stage);
        else if (osName.contains("android")) renderer = new AndroidRenderer(stage);
        else renderer = new JavaFXRenderer(stage);
        renderer.init(appTitle, appWidth, appHeight);
        renderer.mount(appFactory.get());
    }
}


