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
 * Aligns a single child within the available space using one of the standard
 * compass directions.
 */
public final class AlignWidget extends AbstractWidget<AlignWidget> {
    public enum Alignment {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        CENTER_LEFT, CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }

    private final View child;
    private final Alignment alignment;

    public AlignWidget(Alignment alignment, View child) {
        this.alignment = alignment;
        this.child = child;
    }

    public static AlignWidget Align(Alignment a, View child) {
        return new AlignWidget(a, child);
    }

    @Override
    public List<View> getChildren() {
        return child != null ? List.of(child) : List.of();
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        SwingRenderer.applyStyles(panel, getStyle());

        if (child != null) {
            JPanel slot = new JPanel(new java.awt.BorderLayout());
            slot.setOpaque(false);
            swing.pushContainer(slot);
            child.render(renderer);
            swing.popContainer();

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.anchor = anchorFor(alignment);
            panel.add(slot, gbc);
        }
        swing.addComponent(panel);
    }

    private static int anchorFor(Alignment a) {
        return switch (a) {
            case TOP_LEFT -> GridBagConstraints.NORTHWEST;
            case TOP_CENTER -> GridBagConstraints.NORTH;
            case TOP_RIGHT -> GridBagConstraints.NORTHEAST;
            case CENTER_LEFT -> GridBagConstraints.WEST;
            case CENTER -> GridBagConstraints.CENTER;
            case CENTER_RIGHT -> GridBagConstraints.EAST;
            case BOTTOM_LEFT -> GridBagConstraints.SOUTHWEST;
            case BOTTOM_CENTER -> GridBagConstraints.SOUTH;
            case BOTTOM_RIGHT -> GridBagConstraints.SOUTHEAST;
        };
    }
}
