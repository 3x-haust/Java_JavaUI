package io.github._3xhaust.dsl.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;

/**
 * A box of fixed dimensions that optionally wraps a single child. Use
 * {@link #shrink()} for an empty space or supply a child to constrain its size.
 */
public final class SizedBoxWidget extends AbstractWidget<SizedBoxWidget> {
    private final View child;
    private double w;
    private double h;

    public SizedBoxWidget(double width, double height, View child) {
        this.w = width;
        this.h = height;
        this.child = child;
    }

    public static SizedBoxWidget SizedBox(double width, double height) {
        return new SizedBoxWidget(width, height, null);
    }

    public static SizedBoxWidget SizedBox(double width, double height, View child) {
        return new SizedBoxWidget(width, height, child);
    }

    public static SizedBoxWidget shrink() {
        return new SizedBoxWidget(0, 0, null);
    }

    public SizedBoxWidget width(double v) { this.w = v; return this; }
    public SizedBoxWidget height(double v) { this.h = v; return this; }

    @Override
    public List<View> getChildren() {
        return child != null ? List.of(child) : List.of();
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        Dimension size = new Dimension(Math.max(0, (int) w), Math.max(0, (int) h));
        panel.setPreferredSize(size);
        panel.setMinimumSize(size);
        SwingRenderer.applyStyles(panel, getStyle());
        if (child != null) {
            swing.pushContainer(panel);
            child.render(renderer);
            swing.popContainer();
        }
        swing.addComponent(panel);
    }
}
