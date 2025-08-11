package io.github._3xhaust.core;

import io.github._3xhaust.dsl.enums.CrossAxisAlignment;
import io.github._3xhaust.dsl.enums.MainAxisAlignment;
import io.github._3xhaust.state.State;

/**
 * Platform-neutral renderer API. Each backend (Swing, JavaFX, Android, iOS) maps these calls to
 * native UI constructs.
 */
 public interface Renderer {
    // Lifecycle
    void init(String title, int width, int height);
    void mount(View root);
    void update(View oldView, View newView);
    void unmount(View view);

    // Container stack
    void pushColumn(MainAxisAlignment mainAxisAlignment,
                    CrossAxisAlignment crossAxisAlignment,
                    Insets padding,
                    int gap);

    void pushRow(MainAxisAlignment mainAxisAlignment,
                 CrossAxisAlignment crossAxisAlignment,
                 Insets padding,
                 int gap);

    void pushCenter();

    void pushSizedBox(int width, int height);

    void pop();

    // Leaf widgets
    void addText(String text);

    <T> void addText(State<T> state);

    void addButton(String text, Runnable onClick);
}

