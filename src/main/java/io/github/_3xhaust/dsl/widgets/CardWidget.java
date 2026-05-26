package io.github._3xhaust.dsl.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.dsl.EdgeInsets;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.ColorUtils;
import io.github._3xhaust.theme.Theme;

/**
 * Elevated content surface with rounded corners and an optional drop shadow.
 */
public final class CardWidget extends AbstractWidget<CardWidget> {
    private View child;
    private Color background;
    private int radius;
    private int elevation = 2;
    private EdgeInsets padding = EdgeInsets.all(16);

    public CardWidget(View child) {
        this.child = child;
    }

    public static CardWidget Card(View child) {
        return new CardWidget(child);
    }

    public CardWidget child(View v) { this.child = v; return this; }
    public CardWidget backgroundColor(Color c) { this.background = c; return this; }
    public CardWidget radius(int v) { this.radius = v; return this; }
    public CardWidget elevation(int v) { this.elevation = Math.max(0, v); return this; }
    public CardWidget padding(EdgeInsets v) { this.padding = v; return this; }
    public CardWidget padding(int all) { this.padding = EdgeInsets.all(all); return this; }

    @Override
    public List<View> getChildren() {
        return child != null ? List.of(child) : List.of();
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        int r = radius > 0 ? radius : Theme.get().radius();
        Color bg = background != null ? background : Theme.get().surface();
        JPanel card = new ShadowPanel(bg, r, elevation);
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(
                (int) padding.top + elevation,
                (int) padding.left + elevation,
                (int) padding.bottom + elevation,
                (int) padding.right + elevation));
        if (child != null) {
            swing.pushContainer(card);
            child.render(renderer);
            swing.popContainer();
        }
        SwingRenderer.applyStyles(card, getStyle());
        swing.addComponent(card);
    }

    private static final class ShadowPanel extends JPanel {
        private final Color bg;
        private final int radius;
        private final int elevation;

        ShadowPanel(Color bg, int radius, int elevation) {
            this.bg = bg;
            this.radius = radius;
            this.elevation = elevation;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                if (elevation > 0) {
                    Color shadow = ColorUtils.withAlpha(Color.BLACK, 30);
                    for (int i = elevation; i > 0; i--) {
                        g2.setColor(ColorUtils.withAlpha(shadow, 30 - (elevation - i) * 8));
                        g2.fillRoundRect(i, i, w - i * 2, h - i * 2, radius, radius);
                    }
                }
                g2.setColor(bg);
                g2.fillRoundRect(elevation, elevation,
                        w - elevation * 2, h - elevation * 2, radius, radius);
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
