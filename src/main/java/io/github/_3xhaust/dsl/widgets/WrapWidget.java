package io.github._3xhaust.dsl.widgets;

import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;

/**
 * Lays out children left-to-right, wrapping to a new line when the row fills.
 */
public final class WrapWidget extends AbstractWidget<WrapWidget> {
    private final List<View> children;
    private int spacing = 8;
    private int runSpacing = 8;

    public WrapWidget(View... children) {
        this.children = Arrays.asList(children);
    }

    public static WrapWidget Wrap(View... children) {
        return new WrapWidget(children);
    }

    public WrapWidget spacing(int v) { this.spacing = v; return this; }
    public WrapWidget runSpacing(int v) { this.runSpacing = v; return this; }

    @Override
    public List<View> getChildren() { return children; }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, spacing, runSpacing));
        panel.setOpaque(false);
        SwingRenderer.applyStyles(panel, getStyle());
        swing.pushContainer(panel);
        for (View child : children) child.render(renderer);
        swing.popContainer();
        swing.addComponent(panel);
    }
}
