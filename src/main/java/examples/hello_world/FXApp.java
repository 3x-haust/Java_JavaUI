package examples.hello_world;

import io.github._3xhaust.core.App;
import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.platform.javafx.JavaFXRenderer;
import javafx.application.Application;
import javafx.stage.Stage;

public class FXApp extends Application {
    public static View MyApp() {
        return Main.MyApp();
    }

    @Override
    public void start(Stage stage) {
        Renderer renderer = new JavaFXRenderer(stage);
        renderer.init("MyApp", 640, 480);
        renderer.mount(MyApp());
    }

    public static void main(String[] args) {
        launch(args);
    }
}


