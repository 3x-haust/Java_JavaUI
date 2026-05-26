package io.github._3xhaust.dsl;

import java.util.function.Consumer;

public abstract class AbstractWidget<T extends Widget> implements Widget {
    protected final Style style = new Style();

    @Override
    public Style getStyle() {
        return style;
    }
}