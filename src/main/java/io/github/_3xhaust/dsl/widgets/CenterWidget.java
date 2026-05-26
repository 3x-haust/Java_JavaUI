package io.github._3xhaust.dsl.widgets;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;

/**
 * Centers a single child both horizontally and vertically inside the available
 * space. Implemented with {@link GridBagLayout} so it works regardless of the
 * parent layout.
 */
public final class CenterWidget extends AbstractWidget<CenterWidget> {
    private final View child;

    public CenterWidget(View child) {
        this.child = child;
    }

    public static CenterWidget Center(View child) {
        return new CenterWidget(child);
    }

    @Override
    public List<View> getChildren() {
        return child != null ? List.of(child) : List.of();
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;

        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        SwingRenderer.applyStyles(container, getStyle());

        if (child != null) {
            JPanel slot = new JPanel();
            slot.setOpaque(false);
            slot.setLayout(new javax.swing.BoxLayout(slot, javax.swing.BoxLayout.Y_AXIS));
            swing.pushContainer(slot);
            child.render(renderer);
            swing.popContainer();

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            container.add(slot, gbc);
        }
        swing.addComponent(container);
    }
}
