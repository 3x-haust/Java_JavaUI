package io.github._3xhaust.dsl.widgets;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComponent;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.ColorUtils;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.util.Fonts;

/**
 * Material floating action button. Round, primary-colored, with hover lift.
 * Use within {@link ScaffoldWidget}'s {@code floatingActionButton(...)} slot.
 */
public final class FloatingActionButtonWidget extends AbstractWidget<FloatingActionButtonWidget> {
    private final String icon;
    private final Runnable onClick;
    private int size = 56;
    private Color color;

    public FloatingActionButtonWidget(String icon, Runnable onClick) {
        this.icon = icon;
        this.onClick = onClick;
    }

    public static FloatingActionButtonWidget FloatingActionButton(String icon, Runnable onClick) {
        return new FloatingActionButtonWidget(icon, onClick);
    }

    public FloatingActionButtonWidget size(int v) { this.size = v; return this; }
    public FloatingActionButtonWidget color(Color c) { this.color = c; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        FabPane pane = new FabPane();
        SwingRenderer.applyStyles(pane, getStyle());
        swing.addComponent(pane);
    }

    private final class FabPane extends JComponent {
        private boolean hover;

        FabPane() {
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    if (onClick != null) onClick.run();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = size;
                Color bg = color != null ? color : Theme.get().primary();
                if (hover) bg = ColorUtils.lighten(bg, 0.08);
                // shadow
                g2.setColor(ColorUtils.withAlpha(Color.BLACK, 50));
                g2.fillOval(2, 4, s - 4, s - 4);
                g2.setColor(bg);
                g2.fillOval(0, 0, s - 4, s - 4);
                if (icon != null && !icon.isEmpty()) {
                    g2.setColor(ColorUtils.contrasting(bg));
                    g2.setFont(new Font(Fonts.defaultSans(), Font.BOLD, s / 2));
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = ((s - 4) - fm.stringWidth(icon)) / 2;
                    int ty = ((s - 4) - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(icon, tx, ty);
                }
            } finally {
                g2.dispose();
            }
        }
    }
}
