package io.github._3xhaust.dsl.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.ColorUtils;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.util.Fonts;

/**
 * Small label overlaid on the top-right of a child to indicate counts/status.
 */
public final class BadgeWidget extends AbstractWidget<BadgeWidget> {
    private final View child;
    private String label;
    private Color color;

    public BadgeWidget(View child, String label) {
        this.child = child;
        this.label = label;
    }

    public static BadgeWidget Badge(View child, String label) {
        return new BadgeWidget(child, label);
    }

    public BadgeWidget color(Color c) { this.color = c; return this; }
    public BadgeWidget label(String s) { this.label = s; return this; }

    @Override
    public List<View> getChildren() {
        return child != null ? List.of(child) : List.of();
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JLayeredPane layers = new JLayeredPane();
        layers.setOpaque(false);
        JPanel base = new JPanel(new GridBagLayout());
        base.setOpaque(false);
        if (child != null) {
            swing.pushContainer(base);
            child.render(renderer);
            swing.popContainer();
        }
        base.setBounds(0, 0, base.getPreferredSize().width, base.getPreferredSize().height);
        layers.setPreferredSize(base.getPreferredSize());
        layers.add(base, Integer.valueOf(0));

        Pill pill = new Pill(label, color != null ? color : Theme.get().error());
        Dimension pillSize = pill.getPreferredSize();
        pill.setBounds(base.getPreferredSize().width - pillSize.width / 2, 0,
                pillSize.width, pillSize.height);
        layers.add(pill, Integer.valueOf(1));
        // resize pill on layout changes
        layers.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                Dimension d = layers.getSize();
                base.setBounds(0, 0, d.width, d.height);
                Dimension ps = pill.getPreferredSize();
                pill.setBounds(d.width - ps.width / 2, 0, ps.width, ps.height);
            }
        });
        swing.addComponent(layers);
    }

    private static final class Pill extends JComponent {
        private final String text;
        private final Color bg;

        Pill(String text, Color bg) {
            this.text = text == null ? "" : text;
            this.bg = bg;
            FontMetrics fm = getFontMetrics(new Font(Fonts.defaultSans(), Font.BOLD, 10));
            int w = Math.max(16, fm.stringWidth(this.text) + 10);
            int h = 16;
            setPreferredSize(new Dimension(w, h));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, h, h);
                g2.setColor(ColorUtils.contrasting(bg));
                g2.setFont(new Font(Fonts.defaultSans(), Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(text)) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, tx, ty);
            } finally {
                g2.dispose();
            }
        }
    }
}
