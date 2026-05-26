package io.github._3xhaust.dsl.widgets;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.util.Fonts;

/**
 * Renders an icon (any glyph or emoji string). Use the <a href="#shortcuts">Icons</a>
 * helper class for a curated set of common Unicode glyphs.
 */
public final class IconWidget extends AbstractWidget<IconWidget> {
    private final String glyph;
    private int size = 18;
    private Color color;

    public IconWidget(String glyph) {
        this.glyph = glyph;
    }

    public static IconWidget Icon(String glyph) { return new IconWidget(glyph); }

    public IconWidget size(int v) { this.size = v; return this; }
    public IconWidget color(Color c) { this.color = c; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JLabel label = new JLabel(glyph);
        label.setFont(new Font(Fonts.defaultSans(), Font.PLAIN, size));
        label.setForeground(color != null ? color : Theme.get().onSurface());
        SwingRenderer.applyStyles(label, getStyle());
        swing.addComponent(label);
    }
}
