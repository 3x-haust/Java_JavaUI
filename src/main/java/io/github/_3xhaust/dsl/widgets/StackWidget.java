package io.github._3xhaust.dsl.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;

/**
 * Stacks children on top of each other in z-order. Supports an optional
 * preferred size; otherwise the stack sizes itself to the largest child.
 */
public final class StackWidget extends AbstractWidget<StackWidget> {
    private final List<View> children;
    private Integer prefWidth;
    private Integer prefHeight;

    public StackWidget(View... children) {
        this.children = Arrays.asList(children);
    }

    public static StackWidget Stack(View... children) {
        return new StackWidget(children);
    }

    public StackWidget size(int w, int h) {
        this.prefWidth = w;
        this.prefHeight = h;
        return this;
    }

    @Override
    public List<View> getChildren() {
        return children;
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JLayeredPane layers = new JLayeredPane();
        layers.setOpaque(false);
        layers.setLayout(new BorderLayout());

        int maxW = 0, maxH = 0;
        for (int i = 0; i < children.size(); i++) {
            View v = children.get(i);
            JPanel layer = new JPanel(new BorderLayout());
            layer.setOpaque(false);
            swing.pushContainer(layer);
            v.render(renderer);
            swing.popContainer();
            // each layer occupies the whole stack
            layer.setBounds(0, 0, prefWidth != null ? prefWidth : 0, prefHeight != null ? prefHeight : 0);
            layers.add(layer, Integer.valueOf(i));
            Dimension pref = layer.getPreferredSize();
            maxW = Math.max(maxW, pref.width);
            maxH = Math.max(maxH, pref.height);
        }

        Dimension size = new Dimension(
                prefWidth != null ? prefWidth : maxW,
                prefHeight != null ? prefHeight : maxH);
        layers.setPreferredSize(size);

        layers.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                Dimension d = layers.getSize();
                for (Component c : layers.getComponents()) {
                    c.setBounds(0, 0, d.width, d.height);
                }
            }
        });

        swing.addComponent(layers);
    }
}
