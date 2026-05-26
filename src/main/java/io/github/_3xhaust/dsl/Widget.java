package io.github._3xhaust.dsl;

import io.github._3xhaust.core.View;

import java.util.List;

public interface Widget extends View {
    Style getStyle();
    List<View> getChildren();
}