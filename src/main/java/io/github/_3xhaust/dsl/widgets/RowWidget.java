package io.github._3xhaust.dsl.widgets;

import java.awt.Component;
import java.awt.Dimension;

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

import java.util.Arrays;
import java.util.List;

public class RowWidget extends AbstractWidget<RowWidget> {
    private final List<View> children;
    private int gap = 0;
    private MainAxisAlignment mainAxis = MainAxisAlignment.START;
    private CrossAxisAlignment crossAxis = CrossAxisAlignment.CENTER;

    public RowWidget(View... children) {
        this.children = Arrays.asList(children);
    }

    public static RowWidget Row(View... children) {
        return new RowWidget(children);
    }

    public RowWidget gap(int gap) { this.gap = gap; return this; }
    public RowWidget mainAxisAlignment(MainAxisAlignment v) { this.mainAxis = v; return this; }
    public RowWidget crossAxisAlignment(CrossAxisAlignment v) { this.crossAxis = v; return this; }

    @Override
    public List<View> getChildren() {
        return children;
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
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
                    panel.add(Box.createHorizontalGlue());
                } else if (gap > 0) {
                    panel.add(Box.createHorizontalStrut(gap));
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
        // cross-axis
        switch (crossAxis) {
            case START -> jc.setAlignmentY(Component.TOP_ALIGNMENT);
            case CENTER -> jc.setAlignmentY(Component.CENTER_ALIGNMENT);
            case END -> jc.setAlignmentY(Component.BOTTOM_ALIGNMENT);
            case STRETCH -> {
                Dimension max = jc.getMaximumSize();
                jc.setMaximumSize(new Dimension(max.width, Integer.MAX_VALUE));
            }
            case BASELINE -> jc.setAlignmentY(Component.CENTER_ALIGNMENT);
        }
        // main-axis flex via Expanded/Flexible
        if (child instanceof ExpandedWidget ex) {
            int flex = Math.max(1, ex.flex());
            Dimension pref = jc.getPreferredSize();
            jc.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(pref.height, 1)));
            jc.putClientProperty("javaui.flex", flex);
        } else if (child instanceof FlexibleWidget fl) {
            int flex = Math.max(1, fl.flex());
            Dimension pref = jc.getPreferredSize();
            jc.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(pref.height, 1)));
            jc.putClientProperty("javaui.flex", flex);
        }
    }

    private void addLeadingGlue(JPanel p) {
        if (mainAxis == MainAxisAlignment.END
                || mainAxis == MainAxisAlignment.CENTER
                || mainAxis == MainAxisAlignment.SPACE_AROUND
                || mainAxis == MainAxisAlignment.SPACE_EVENLY) {
            p.add(Box.createHorizontalGlue());
        }
    }

    private void addTrailingGlue(JPanel p) {
        if (mainAxis == MainAxisAlignment.START
                || mainAxis == MainAxisAlignment.CENTER
                || mainAxis == MainAxisAlignment.SPACE_AROUND
                || mainAxis == MainAxisAlignment.SPACE_EVENLY) {
            p.add(Box.createHorizontalGlue());
        }
    }
}
