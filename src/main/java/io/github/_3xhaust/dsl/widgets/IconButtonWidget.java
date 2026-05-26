package io.github._3xhaust.dsl.widgets;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
import io.github._3xhaust.util.Fonts;

/**
 * Compact icon-only button. Pass any text (e.g. an emoji) as the icon.
 */
public final class IconButtonWidget extends AbstractWidget<IconButtonWidget> {
    private final String icon;
    private final Runnable onClick;
    private int size = 36;
    private Color color;

    public IconButtonWidget(String icon, Runnable onClick) {
        this.icon = icon;
        this.onClick = onClick;
    }

    public static IconButtonWidget IconButton(String icon, Runnable onClick) {
        return new IconButtonWidget(icon, onClick);
    }

    public IconButtonWidget size(int v) { this.size = v; return this; }
    public IconButtonWidget color(Color c) { this.color = c; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JButton btn = new JButton(icon);
        btn.setFont(new Font(Fonts.defaultSans(), Font.PLAIN, Math.max(12, size / 2)));
        btn.setForeground(color != null ? color : Theme.get().onSurface());
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(size, size));
        btn.setMaximumSize(new Dimension(size, size));
        btn.setMinimumSize(new Dimension(size, size));
        if (onClick != null) btn.addActionListener(e -> onClick.run());
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setOpaque(true);
                btn.setBackground(ColorUtils.withAlpha(Theme.get().onSurface(), 24));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setOpaque(false);
                btn.setBackground(null);
            }
        });
        SwingRenderer.applyStyles(btn, getStyle());
        swing.addComponent(btn);
    }
}
