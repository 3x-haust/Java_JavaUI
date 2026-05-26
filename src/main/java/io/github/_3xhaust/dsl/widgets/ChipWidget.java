package io.github._3xhaust.dsl.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.ColorUtils;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.util.Fonts;

/**
 * Compact rounded label, optionally clickable.
 */
public final class ChipWidget extends AbstractWidget<ChipWidget> {
    private final String text;
    private Runnable onClick;
    private Color color;

    public ChipWidget(String text) {
        this.text = text;
    }

    public static ChipWidget Chip(String text) {
        return new ChipWidget(text);
    }

    public ChipWidget onClick(Runnable r) { this.onClick = r; return this; }
    public ChipWidget color(Color c) { this.color = c; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        Color bg = color != null ? color : ColorUtils.withAlpha(Theme.get().primary(), 40);
        JPanel chip = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                try {
                    g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                            java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        chip.setOpaque(false);
        chip.setBorder(new EmptyBorder(4, 12, 4, 12));
        JLabel label = new JLabel(text);
        label.setFont(new Font(Fonts.defaultSans(), Font.PLAIN, 12));
        label.setForeground(ColorUtils.contrasting(bg));
        chip.add(label, BorderLayout.CENTER);
        if (onClick != null) {
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            chip.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
            });
        }
        SwingRenderer.applyStyles(chip, getStyle());
        swing.addComponent(chip);
    }
}
