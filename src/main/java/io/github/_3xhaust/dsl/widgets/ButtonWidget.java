package io.github._3xhaust.dsl.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.ColorUtils;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.theme.ThemeData;
import io.github._3xhaust.util.Fonts;

/**
 * Material-flavored button with three variants:
 * <ul>
 *   <li>{@link Variant#FILLED} - default, primary background.</li>
 *   <li>{@link Variant#OUTLINED} - transparent with primary border.</li>
 *   <li>{@link Variant#TEXT} - flat, primary foreground.</li>
 * </ul>
 */
public final class ButtonWidget extends AbstractWidget<ButtonWidget> {
    public enum Variant { FILLED, OUTLINED, TEXT }

    private final String text;
    private final Runnable onClick;
    private Variant variant = Variant.FILLED;
    private Color overrideColor;
    private Color overrideBackground;
    private boolean enabled = true;

    public ButtonWidget(String text, Runnable onClick) {
        this.text = text;
        this.onClick = onClick;
    }

    public static ButtonWidget Button(String text, Runnable onClick) {
        return new ButtonWidget(text, onClick);
    }

    public static ButtonWidget Button(String text) {
        return new ButtonWidget(text, null);
    }

    public ButtonWidget filled() { this.variant = Variant.FILLED; return this; }
    public ButtonWidget outlined() { this.variant = Variant.OUTLINED; return this; }
    public ButtonWidget text() { this.variant = Variant.TEXT; return this; }
    public ButtonWidget variant(Variant v) { this.variant = v; return this; }
    public ButtonWidget enabled(boolean v) { this.enabled = v; return this; }
    public ButtonWidget color(Color c) { this.overrideColor = c; return this; }
    public ButtonWidget backgroundColor(Color c) { this.overrideBackground = c; return this; }
    public ButtonWidget fontSize(int size) { getStyle().fontSize(size); return this; }
    public ButtonWidget bold() { getStyle().bold(); return this; }
    public ButtonWidget padding(int all) { getStyle().padding(all); return this; }
    public ButtonWidget padding(int top, int right, int bottom, int left) {
        getStyle().padding(top, left, bottom, right);
        return this;
    }

    public String getText() { return text; }
    public Runnable getOnClick() { return onClick; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        ThemeData theme = Theme.get();
        StyledButton button = new StyledButton(text == null ? "" : text, variant,
                effectiveBackground(theme), effectiveForeground(theme), theme);
        if (getStyle().getFont() != null) button.setFont(getStyle().getFont());
        else button.setFont(new Font(Fonts.defaultSans(), Font.PLAIN, 14));
        button.setEnabled(enabled);
        Insets pad = getStyle().getPadding();
        if (pad != null) button.setMargin(pad);
        button.addActionListener(e -> {
            if (onClick != null) onClick.run();
        });
        SwingRenderer.applyStyles(button, getStyle());
        swing.addComponent(button);
    }

    private Color effectiveBackground(ThemeData theme) {
        if (overrideBackground != null) return overrideBackground;
        return switch (variant) {
            case FILLED -> theme.primary();
            case OUTLINED, TEXT -> new Color(0, 0, 0, 0);
        };
    }

    private Color effectiveForeground(ThemeData theme) {
        if (overrideColor != null) return overrideColor;
        return switch (variant) {
            case FILLED -> theme.onPrimary();
            case OUTLINED, TEXT -> theme.primary();
        };
    }

    private static final class StyledButton extends JButton {
        private final Variant variant;
        private final Color baseBg;
        private final Color baseFg;
        private final ThemeData theme;
        private boolean hovering;
        private boolean pressed;

        StyledButton(String text, Variant variant, Color bg, Color fg, ThemeData theme) {
            super(text);
            this.variant = variant;
            this.baseBg = bg;
            this.baseFg = fg;
            this.theme = theme;
            setForeground(fg);
            setBackground(bg);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setMargin(new Insets(8, 16, 8, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovering = false; pressed = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            Insets m = getMargin();
            int w = fm.stringWidth(getText()) + m.left + m.right + 8;
            int h = fm.getHeight() + m.top + m.bottom + 4;
            return new Dimension(w, h);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int radius = theme.radius();

                Color fg = isEnabled() ? baseFg : theme.disabled();
                Color bg = currentBg();
                if (variant == Variant.FILLED) {
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, w, h, radius, radius);
                } else if (variant == Variant.OUTLINED) {
                    if (hovering) {
                        g2.setColor(theme.hover());
                        g2.fillRoundRect(0, 0, w, h, radius, radius);
                    }
                    g2.setColor(isEnabled() ? baseFg : theme.disabled());
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
                } else {
                    if (hovering) {
                        g2.setColor(theme.hover());
                        g2.fillRoundRect(0, 0, w, h, radius, radius);
                    }
                }

                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
            } finally {
                g2.dispose();
            }
        }

        private Color currentBg() {
            if (variant != Variant.FILLED) return baseBg;
            if (!isEnabled()) return ColorUtils.darken(theme.disabled(), 0.05);
            if (pressed) return ColorUtils.darken(baseBg, 0.10);
            if (hovering) return ColorUtils.darken(baseBg, 0.04);
            return baseBg;
        }
    }
}
