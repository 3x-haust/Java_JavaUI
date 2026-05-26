package io.github._3xhaust.dsl.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.Theme;

/**
 * Thin horizontal or vertical line. Defaults to the active theme's outline color.
 */
public final class DividerWidget extends AbstractWidget<DividerWidget> {
    private boolean horizontal = true;
    private int thickness = 1;
    private Color color;

    public static DividerWidget Divider() {
        return new DividerWidget();
    }

    public static DividerWidget VerticalDivider() {
        DividerWidget d = new DividerWidget();
        d.horizontal = false;
        return d;
    }

    public DividerWidget thickness(int v) { this.thickness = Math.max(1, v); return this; }
    public DividerWidget color(Color c) { this.color = c; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JSeparator sep = new JSeparator(horizontal ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL);
        sep.setForeground(color != null ? color : Theme.get().outline());
        sep.setBackground(sep.getForeground());
        if (horizontal) {
            sep.setPreferredSize(new Dimension(Integer.MAX_VALUE, thickness));
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, thickness));
        } else {
            sep.setPreferredSize(new Dimension(thickness, Integer.MAX_VALUE));
            sep.setMaximumSize(new Dimension(thickness, Integer.MAX_VALUE));
        }
        swing.addComponent(sep);
    }
}
