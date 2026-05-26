package io.github._3xhaust.dsl.widgets;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.dsl.EdgeInsets;
import io.github._3xhaust.platform.swing.SwingRenderer;

/**
 * Wraps a child with empty space on all four sides.
 */
public final class PaddingWidget extends AbstractWidget<PaddingWidget> {
    private final EdgeInsets insets;
    private final View child;

    public PaddingWidget(EdgeInsets insets, View child) {
        this.insets = insets;
        this.child = child;
    }

    public static PaddingWidget Padding(EdgeInsets insets, View child) {
        return new PaddingWidget(insets, child);
    }

    public static PaddingWidget Padding(int all, View child) {
        return new PaddingWidget(EdgeInsets.all(all), child);
    }

    @Override
    public List<View> getChildren() {
        return child != null ? List.of(child) : List.of();
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder((int) insets.top, (int) insets.left,
                (int) insets.bottom, (int) insets.right));
        if (child != null) {
            swing.pushContainer(panel);
            child.render(renderer);
            swing.popContainer();
        }
        swing.addComponent(panel);
    }
}
