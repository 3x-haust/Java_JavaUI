package io.github._3xhaust.platform.javafx;

import io.github._3xhaust.core.Insets;
import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.enums.CrossAxisAlignment;
import io.github._3xhaust.dsl.enums.MainAxisAlignment;
import io.github._3xhaust.state.State;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.ArrayDeque;
import java.util.Deque;

public class JavaFXRenderer implements Renderer {
    private final Stage stage;
    private final Deque<ContainerContext> stack = new ArrayDeque<>();

    private static class ContainerContext {
        final Pane pane;
        final String type; // column, row, center, sizedBox
        final MainAxisAlignment mainAxisAlignment;
        final CrossAxisAlignment crossAxisAlignment;
        final int gap;
        boolean firstChild = true;

        ContainerContext(Pane pane, String type,
                         MainAxisAlignment mainAxisAlignment,
                         CrossAxisAlignment crossAxisAlignment,
                         int gap) {
            this.pane = pane;
            this.type = type;
            this.mainAxisAlignment = mainAxisAlignment;
            this.crossAxisAlignment = crossAxisAlignment;
            this.gap = gap;
        }
    }

    public JavaFXRenderer(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void init(String title, int width, int height) {
        Runnable setup = () -> {
            VBox root = new VBox();
            root.setFillWidth(true);
            Scene scene = new Scene(root, width, height);
            scene.setFill(Color.WHITE);
            root.setStyle("-fx-background-color: white;");
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
            stack.clear();
            stack.push(new ContainerContext(root, "column", MainAxisAlignment.START, CrossAxisAlignment.START, 0));
        };
        if (Platform.isFxApplicationThread()) {
            setup.run();
        } else {
            Platform.runLater(setup);
        }
    }

    @Override
    public void mount(View root) {
        Platform.runLater(() -> {
            if (stack.isEmpty()) return;
            ContainerContext ctx = stack.peek();
            ctx.pane.getChildren().clear();
            ctx.firstChild = true;
            root.render(this);
        });
    }

    @Override
    public void update(View oldView, View newView) {
        // TODO: diff/reconcile
    }

    @Override
    public void unmount(View view) {
    }

    private void attachToParent(Pane child) {
        if (!stack.isEmpty()) {
            ContainerContext parent = stack.peek();
            if (parent.pane instanceof VBox) {
                ((VBox) parent.pane).getChildren().add(child);
            } else if (parent.pane instanceof HBox) {
                ((HBox) parent.pane).getChildren().add(child);
            } else if (parent.pane instanceof StackPane) {
                ((StackPane) parent.pane).getChildren().add(child);
            } else {
                parent.pane.getChildren().add(child);
            }
        }
    }

    private void configurePadding(Pane pane, Insets padding) {
        if (padding != null) {
            pane.setPadding(new javafx.geometry.Insets(padding.top, padding.right, padding.bottom, padding.left));
        }
    }

    private void push(Pane pane, String type,
                      MainAxisAlignment mainAxisAlignment,
                      CrossAxisAlignment crossAxisAlignment,
                      int gap) {
        attachToParent(pane);
        if (pane instanceof VBox vbox) {
            vbox.setSpacing(Math.max(0, gap));
            vbox.setFillWidth(true);
            switch (crossAxisAlignment) {
                case START -> vbox.setAlignment(Pos.TOP_LEFT);
                case CENTER -> vbox.setAlignment(Pos.TOP_CENTER);
                case END -> vbox.setAlignment(Pos.TOP_RIGHT);
                case STRETCH -> vbox.setAlignment(Pos.TOP_LEFT);
                default -> vbox.setAlignment(Pos.TOP_CENTER);
            }
        } else if (pane instanceof HBox hbox) {
            hbox.setSpacing(Math.max(0, gap));
            switch (crossAxisAlignment) {
                case START -> hbox.setAlignment(Pos.TOP_LEFT);
                case CENTER -> hbox.setAlignment(Pos.CENTER_LEFT);
                case END -> hbox.setAlignment(Pos.BOTTOM_LEFT);
                case STRETCH -> hbox.setAlignment(Pos.CENTER_LEFT);
                default -> hbox.setAlignment(Pos.CENTER_LEFT);
            }
        } else if (pane instanceof StackPane stackPane) {
            stackPane.setAlignment(Pos.CENTER);
        }
        stack.push(new ContainerContext(pane, type, mainAxisAlignment, crossAxisAlignment, gap));
    }

    @Override
    public void pushColumn(MainAxisAlignment mainAxisAlignment, CrossAxisAlignment crossAxisAlignment, Insets padding, int gap) {
        VBox vbox = new VBox();
        configurePadding(vbox, padding);
        push(vbox, "column", mainAxisAlignment, crossAxisAlignment, gap);
    }

    @Override
    public void pushRow(MainAxisAlignment mainAxisAlignment, CrossAxisAlignment crossAxisAlignment, Insets padding, int gap) {
        HBox hbox = new HBox();
        configurePadding(hbox, padding);
        push(hbox, "row", mainAxisAlignment, crossAxisAlignment, gap);
    }

    @Override
    public void pushCenter() {
        StackPane pane = new StackPane();
        push(pane, "center", MainAxisAlignment.CENTER, CrossAxisAlignment.CENTER, 0);
    }

    @Override
    public void pushSizedBox(int width, int height) {
        Pane pane = new Pane();
        pane.setMinSize(width, height);
        pane.setPrefSize(width, height);
        pane.setMaxSize(width, height);
        push(pane, "sizedBox", MainAxisAlignment.START, CrossAxisAlignment.START, 0);
    }

    @Override
    public void pop() {
        if (!stack.isEmpty()) stack.pop();
    }

    @Override
    public void addText(String text) {
        Label label = new Label(text);
        if (!stack.isEmpty()) {
            ContainerContext parent = stack.peek();
            if (parent.pane instanceof VBox vbox) vbox.getChildren().add(label);
            else if (parent.pane instanceof HBox hbox) hbox.getChildren().add(label);
            else if (parent.pane instanceof StackPane stackPane) stackPane.getChildren().add(label);
            else parent.pane.getChildren().add(label);
        }
    }

    @Override
    public <T> void addText(State<T> state) {
        Label label = new Label(state.get() != null ? state.get().toString() : "");
        state.bindOnChange(newVal -> Platform.runLater(() -> label.setText(newVal != null ? newVal.toString() : "")));
        if (!stack.isEmpty()) {
            ContainerContext parent = stack.peek();
            if (parent.pane instanceof VBox vbox) vbox.getChildren().add(label);
            else if (parent.pane instanceof HBox hbox) hbox.getChildren().add(label);
            else if (parent.pane instanceof StackPane stackPane) stackPane.getChildren().add(label);
            else parent.pane.getChildren().add(label);
        }
    }

    @Override
    public void addButton(String text, Runnable onClick) {
        Button button = new Button(text);
        button.setOnAction(e -> onClick.run());
        if (!stack.isEmpty()) {
            ContainerContext parent = stack.peek();
            if (parent.pane instanceof VBox vbox) vbox.getChildren().add(button);
            else if (parent.pane instanceof HBox hbox) hbox.getChildren().add(button);
            else if (parent.pane instanceof StackPane stackPane) stackPane.getChildren().add(button);
            else parent.pane.getChildren().add(button);
        }
    }
}


