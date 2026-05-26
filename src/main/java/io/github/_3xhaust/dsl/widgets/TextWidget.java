package io.github._3xhaust.dsl.widgets;

import java.awt.Color;
import java.util.List;

import javax.swing.JLabel;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.Theme;

/**
 * Static text label. Use {@code Mod.size(n)}/{@code Mod.weightBold()} or the
 * fluent helpers to style.
 */
public final class TextWidget extends AbstractWidget<TextWidget> {
    private final String text;
    private boolean themed = true;

    public TextWidget(String text) {
        this.text = text;
    }

    public static TextWidget Text(String text) {
        return new TextWidget(text);
    }

    public String getText() { return text; }

    public TextWidget fontSize(int size) { getStyle().fontSize(size); return this; }
    public TextWidget bold() { getStyle().bold(); return this; }
    public TextWidget italic() { getStyle().italic(); return this; }
    public TextWidget color(Color c) { themed = false; getStyle().color(c); return this; }
    public TextWidget color(int r, int g, int b) { themed = false; getStyle().color(r, g, b); return this; }
    public TextWidget backgroundColor(Color c) { getStyle().backgroundColor(c); return this; }
    public TextWidget alignLeft() { getStyle().alignLeft(); return this; }
    public TextWidget alignCenter() { getStyle().alignCenter(); return this; }
    public TextWidget alignRight() { getStyle().alignRight(); return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JLabel label = new JLabel(text == null ? "" : text);
        if (themed && getStyle().getForegroundColor() == null) {
            label.setForeground(Theme.get().onBackground());
        }
        SwingRenderer.applyStyles(label, getStyle());
        swing.addComponent(label);
    }
}
