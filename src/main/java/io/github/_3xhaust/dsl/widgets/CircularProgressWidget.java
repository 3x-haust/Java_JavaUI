package io.github._3xhaust.dsl.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.Timer;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.Theme;

/**
 * Spinning circular indicator. Pure-Java drawing - no external dependencies.
 */
public final class CircularProgressWidget extends AbstractWidget<CircularProgressWidget> {
    private int size = 36;
    private int stroke = 4;
    private Color color;

    public static CircularProgressWidget CircularProgress() {
        return new CircularProgressWidget();
    }

    public CircularProgressWidget size(int v) { this.size = v; return this; }
    public CircularProgressWidget stroke(int v) { this.stroke = v; return this; }
    public CircularProgressWidget color(Color c) { this.color = c; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        Spinner s = new Spinner(size, stroke, color != null ? color : Theme.get().primary());
        SwingRenderer.applyStyles(s, getStyle());
        swing.addComponent(s);
    }

    private static final class Spinner extends JComponent {
        private final int size;
        private final int stroke;
        private final Color color;
        private final Timer timer;
        private int angle;

        Spinner(int size, int stroke, Color color) {
            this.size = size;
            this.stroke = stroke;
            this.color = color;
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setOpaque(false);
            timer = new Timer(40, e -> {
                angle = (angle + 12) % 360;
                repaint();
            });
        }

        @Override
        public void addNotify() {
            super.addNotify();
            timer.start();
        }

        @Override
        public void removeNotify() {
            timer.stop();
            super.removeNotify();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int pad = stroke;
                int s = size - pad * 2;
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
                g2.drawOval(pad, pad, s, s);
                g2.setColor(color);
                g2.drawArc(pad, pad, s, s, angle, 90);
            } finally {
                g2.dispose();
            }
        }
    }
}
