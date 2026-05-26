package io.github._3xhaust.dsl.widgets;

import java.util.List;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;

/**
 * Like {@link ExpandedWidget} but does not force the child to occupy all of its
 * available space along the main axis - it can be smaller than its slot.
 */
public final class FlexibleWidget implements View {
    private final View child;
    private final int flex;

    public FlexibleWidget(View child, int flex) {
        this.child = child;
        this.flex = Math.max(1, flex);
    }

    public static FlexibleWidget Flexible(View child) {
        return new FlexibleWidget(child, 1);
    }

    public static FlexibleWidget Flexible(int flex, View child) {
        return new FlexibleWidget(child, flex);
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
