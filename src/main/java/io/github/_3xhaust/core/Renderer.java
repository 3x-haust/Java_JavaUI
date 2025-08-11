package io.github._3xhaust.core;

import java.awt.*;
import java.util.List;

public interface Renderer {
    void init(String title, int width, int height);
    void mount(View root);
    void update(View oldView, View newView);
    void unmount(View view);

    void renderContainer(String type, List<View> children);
    void pushContainer(Container container);
    void popContainer();
    void addComponent(Component comp);
}

