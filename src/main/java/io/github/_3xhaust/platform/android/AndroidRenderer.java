package io.github._3xhaust.platform.android;

import io.github._3xhaust.platform.javafx.JavaFXRenderer;
import javafx.stage.Stage;

/**
 * Android 전용 Renderer. 현재는 JavaFXRenderer를 그대로 사용하며
 * 패키지 네임스페이스만 OS 기준으로 제공한다.
 */
public class AndroidRenderer extends JavaFXRenderer {
    public AndroidRenderer(Stage stage) {
        super(stage);
    }
}


