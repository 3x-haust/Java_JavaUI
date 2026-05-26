package io.github._3xhaust.dsl.widgets;

import java.util.List;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;

/**
 * Causes the wrapped child to fill the remaining space along the main axis of a
 * {@link RowWidget} or {@link ColumnWidget}. The {@code flex} value determines the
 * proportional share when multiple Expanded siblings are present.
 */
public final class ExpandedWidget implements View {
    private final View child;
    private final int flex;

    public ExpandedWidget(View child, int flex) {
        this.child = child;
        this.flex = Math.max(1, flex);
    }

    public static ExpandedWidget Expanded(View child) {
        return new ExpandedWidget(child, 1);
    }

    public static ExpandedWidget Expanded(int flex, View child) {
        return new ExpandedWidget(child, flex);
    }

    public int flex() {
        return flex;
    }

    public View child() {
        return child;
    }

    public List<View> getChildren() {
        return child == null ? List.of() : List.of(child);
    }

    @Override
    public void render(Renderer renderer) {
        if (child != null) child.render(renderer);
    }
}
