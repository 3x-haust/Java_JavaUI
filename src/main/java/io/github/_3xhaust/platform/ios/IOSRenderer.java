package io.github._3xhaust.platform.ios;

import io.github._3xhaust.platform.javafx.JavaFXRenderer;
import javafx.stage.Stage;

/**
 * iOS 전용 Renderer. 내부적으로 JavaFXRenderer를 사용하지만, OS 관점의 패키지 구조를 제공.
 */
public class IOSRenderer extends JavaFXRenderer {
    public IOSRenderer(Stage stage) {
        super(stage);
    }
}


