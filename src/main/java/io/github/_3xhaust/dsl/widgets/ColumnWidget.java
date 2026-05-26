package io.github._3xhaust.dsl.widgets;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.dsl.enums.CrossAxisAlignment;
import io.github._3xhaust.dsl.enums.MainAxisAlignment;
import io.github._3xhaust.platform.swing.SwingRenderer;

public class ColumnWidget extends AbstractWidget<ColumnWidget> {
    private final List<View> children;
    private int gap = 0;
    private MainAxisAlignment mainAxis = MainAxisAlignment.START;
    private CrossAxisAlignment crossAxis = CrossAxisAlignment.START;

    public ColumnWidget(View... children) {
        this.children = Arrays.asList(children);
    }

    public static ColumnWidget Column(View... children) {
        return new ColumnWidget(children);
    }

    public ColumnWidget gap(int gap) { this.gap = gap; return this; }
    public ColumnWidget mainAxisAlignment(MainAxisAlignment v) { this.mainAxis = v; return this; }
    public ColumnWidget crossAxisAlignment(CrossAxisAlignment v) { this.crossAxis = v; return this; }

    @Override
    public List<View> getChildren() {
        return children;
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        SwingRenderer.applyStyles(panel, getStyle());

        addLeadingGlue(panel);
        for (int i = 0; i < children.size(); i++) {
            View child = children.get(i);
            renderChild(swing, panel, child);
            if (i < children.size() - 1) {
                if (mainAxis == MainAxisAlignment.SPACE_BETWEEN
                        || mainAxis == MainAxisAlignment.SPACE_AROUND
                        || mainAxis == MainAxisAlignment.SPACE_EVENLY) {
                    panel.add(Box.createVerticalGlue());
                } else if (gap > 0) {
                    panel.add(Box.createVerticalStrut(gap));
                }
            }
        }
        addTrailingGlue(panel);
        swing.addComponent(panel);
    }

    private void renderChild(SwingRenderer swing, JPanel parent, View child) {
        int before = parent.getComponentCount();
        swing.pushContainer(parent);
        child.render(swing);
        swing.popContainer();
        int after = parent.getComponentCount();
        if (after <= before) return;
        Component c = parent.getComponent(after - 1);
        applyAxisHints(c, child);
    }

    private void applyAxisHints(Component c, View child) {
        if (!(c instanceof JComponent jc)) return;
        switch (crossAxis) {
            case START -> jc.setAlignmentX(Component.LEFT_ALIGNMENT);
            case CENTER -> jc.setAlignmentX(Component.CENTER_ALIGNMENT);
            case END -> jc.setAlignmentX(Component.RIGHT_ALIGNMENT);
            case STRETCH -> {
                Dimension max = jc.getMaximumSize();
                jc.setMaximumSize(new Dimension(Integer.MAX_VALUE, max.height));
                jc.setAlignmentX(Component.CENTER_ALIGNMENT);
            }
            case BASELINE -> jc.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        if (child instanceof ExpandedWidget ex) {
            int flex = Math.max(1, ex.flex());
            Dimension pref = jc.getPreferredSize();
            jc.setMaximumSize(new Dimension(Math.max(pref.width, 1), Integer.MAX_VALUE));
            jc.putClientProperty("javaui.flex", flex);
        } else if (child instanceof FlexibleWidget fl) {
            int flex = Math.max(1, fl.flex());
            Dimension pref = jc.getPreferredSize();
            jc.setMaximumSize(new Dimension(Math.max(pref.width, 1), Integer.MAX_VALUE));
            jc.putClientProperty("javaui.flex", flex);
        }
    }

    private void addLeadingGlue(JPanel p) {
        if (mainAxis == MainAxisAlignment.END
                || mainAxis == MainAxisAlignment.CENTER
                || mainAxis == MainAxisAlignment.SPACE_AROUND
                || mainAxis == MainAxisAlignment.SPACE_EVENLY) {
            p.add(Box.createVerticalGlue());
        }
    }

    private void addTrailingGlue(JPanel p) {
        if (mainAxis == MainAxisAlignment.START
                || mainAxis == MainAxisAlignment.CENTER
                || mainAxis == MainAxisAlignment.SPACE_AROUND
                || mainAxis == MainAxisAlignment.SPACE_EVENLY) {
            p.add(Box.createVerticalGlue());
        }
    }
}
