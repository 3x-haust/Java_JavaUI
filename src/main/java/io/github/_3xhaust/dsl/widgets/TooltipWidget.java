package io.github._3xhaust.dsl.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;

/**
 * Adds a tooltip to its child. The tooltip is shown by Swing when the mouse hovers
 * for the platform-default delay.
 */
public final class TooltipWidget extends AbstractWidget<TooltipWidget> {
    private final String message;
    private final View child;

    public TooltipWidget(String message, View child) {
        this.message = message;
        this.child = child;
    }

    public static TooltipWidget Tooltip(String message, View child) {
        return new TooltipWidget(message, child);
    }

    @Override
    public List<View> getChildren() {
        return child != null ? List.of(child) : List.of();
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setToolTipText(message);
        if (child != null) {
            swing.pushContainer(wrap);
            child.render(renderer);
            swing.popContainer();
            // Apply tooltip to the actual rendered child too
            for (Component c : wrap.getComponents()) {
                if (c instanceof JComponent jc) jc.setToolTipText(message);
            }
        }
        swing.addComponent(wrap);
    }
}
