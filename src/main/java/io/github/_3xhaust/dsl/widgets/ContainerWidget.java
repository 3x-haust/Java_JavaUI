package io.github._3xhaust.dsl.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.dsl.EdgeInsets;
import io.github._3xhaust.platform.swing.SwingRenderer;

/**
 * Generic box with optional padding, background color, border and rounded corners.
 * The Swing-rendered version draws an antialiased rounded background when
 * {@link #radius(int)} is set, so corners are crisp on HiDPI displays.
 */
public final class ContainerWidget extends AbstractWidget<ContainerWidget> {
    private View child;
    private Color backgroundColor;
    private Color borderColor;
    private int borderWidth;
    private int radius;
    private EdgeInsets padding;
    private Integer fixedWidth;
    private Integer fixedHeight;

    public ContainerWidget(View child) {
        this.child = child;
    }

    public ContainerWidget() {
        this(null);
    }

    public static ContainerWidget Container(View child) {
        return new ContainerWidget(child);
    }

    public static ContainerWidget Container() {
        return new ContainerWidget();
    }

    public ContainerWidget child(View v) { this.child = v; return this; }
    public ContainerWidget backgroundColor(Color v) { this.backgroundColor = v; return this; }
    public ContainerWidget border(int width, Color color) {
        this.borderWidth = width; this.borderColor = color; return this;
    }
    public ContainerWidget radius(int v) { this.radius = Math.max(0, v); return this; }
    public ContainerWidget padding(EdgeInsets v) { this.padding = v; return this; }
    public ContainerWidget padding(int all) { this.padding = EdgeInsets.all(all); return this; }
    public ContainerWidget padding(int top, int right, int bottom, int left) {
        this.padding = new EdgeInsets(top, right, bottom, left); return this;
    }
    public ContainerWidget width(int v) { this.fixedWidth = v; return this; }
    public ContainerWidget height(int v) { this.fixedHeight = v; return this; }
    public ContainerWidget size(int w, int h) {
        this.fixedWidth = w; this.fixedHeight = h; return this;
    }

    @Override
    public List<View> getChildren() {
        return child != null ? List.of(child) : List.of();
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;

        JPanel panel = new RoundedPanel(backgroundColor, borderColor, borderWidth, radius);
        panel.setLayout(new BorderLayout());
        if (padding != null) {
            panel.setBorder(new EmptyBorder((int) padding.top, (int) padding.left,
                    (int) padding.bottom, (int) padding.right));
        }
        SwingRenderer.applyStyles(panel, getStyle());
        if (fixedWidth != null || fixedHeight != null) {
            int w = fixedWidth != null ? fixedWidth : panel.getPreferredSize().width;
            int h = fixedHeight != null ? fixedHeight : panel.getPreferredSize().height;
            panel.setPreferredSize(new Dimension(w, h));
        }

        if (child != null) {
            swing.pushContainer(panel);
            child.render(renderer);
            swing.popContainer();
        }
        swing.addComponent(panel);
    }

    private static final class RoundedPanel extends JPanel {
        private final Color bg;
        private final Color border;
        private final int borderWidth;
        private final int radius;

        RoundedPanel(Color bg, Color border, int borderWidth, int radius) {
            this.bg = bg;
            this.border = border;
            this.borderWidth = borderWidth;
            this.radius = radius;
            setOpaque(bg == null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (bg == null && border == null) {
                super.paintComponent(g);
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                if (radius > 0) {
                    if (bg != null) {
                        g2.setColor(bg);
                        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));
                    }
                    if (border != null && borderWidth > 0) {
                        g2.setColor(border);
                        g2.setStroke(new java.awt.BasicStroke(borderWidth));
                        g2.draw(new RoundRectangle2D.Float(borderWidth / 2f, borderWidth / 2f,
                                w - borderWidth, h - borderWidth, radius, radius));
                    }
                } else {
                    if (bg != null) {
                        g2.setColor(bg);
                        g2.fillRect(0, 0, w, h);
                    }
                    if (border != null && borderWidth > 0) {
                        g2.setColor(border);
                        g2.setStroke(new java.awt.BasicStroke(borderWidth));
                        g2.drawRect(0, 0, w - 1, h - 1);
                    }
                }
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
