package io.github._3xhaust.dsl.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
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
 * Top application bar with optional leading widget and trailing action widgets.
 */
public final class AppBarWidget extends AbstractWidget<AppBarWidget> {
    private final View title;
    private View leading;
    private final List<View> actions = new ArrayList<>();
    private Color backgroundColor;
    private double height = 56;

    public AppBarWidget(View title) {
        this.title = title;
    }

    public static AppBarWidget AppBar(View title) {
        return new AppBarWidget(title);
    }

    public static AppBarWidget AppBar(String title) {
        return new AppBarWidget(TextWidget.Text(title));
    }

    public AppBarWidget backgroundColor(Color c) { this.backgroundColor = c; return this; }
    public AppBarWidget height(double v) { this.height = v; return this; }
    public AppBarWidget leading(View v) { this.leading = v; return this; }

    public AppBarWidget action(View v) {
        actions.add(v);
        return this;
    }

    @Override
    public List<View> getChildren() {
        List<View> out = new ArrayList<>();
        if (leading != null) out.add(leading);
        if (title != null) out.add(title);
        out.addAll(actions);
        return out;
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        Color bg = backgroundColor != null ? backgroundColor : Theme.get().primary();
        Color fg = ColorUtils.contrasting(bg);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);
        panel.setPreferredSize(new Dimension(0, (int) height));
        panel.setBorder(new EmptyBorder(0, 12, 0, 12));

        if (leading != null) {
            JPanel leadingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            leadingPanel.setOpaque(false);
            swing.pushContainer(leadingPanel);
            leading.render(renderer);
            swing.popContainer();
            recolorChildren(leadingPanel, fg);
            panel.add(leadingPanel, BorderLayout.WEST);
        }

        if (title != null) {
            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            titlePanel.setOpaque(false);
            swing.pushContainer(titlePanel);
            title.render(renderer);
            swing.popContainer();
            for (java.awt.Component c : titlePanel.getComponents()) {
                if (c instanceof JLabel l) {
                    l.setForeground(fg);
                    l.setFont(new Font(Fonts.defaultSans(), Font.BOLD, 18));
                }
            }
            panel.add(titlePanel, BorderLayout.CENTER);
        }

        if (!actions.isEmpty()) {
            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            actionsPanel.setOpaque(false);
            for (View action : actions) {
                swing.pushContainer(actionsPanel);
                action.render(renderer);
                swing.popContainer();
            }
            recolorChildren(actionsPanel, fg);
            panel.add(actionsPanel, BorderLayout.EAST);
        }

        SwingRenderer.applyStyles(panel, getStyle());
        swing.addComponent(panel);
    }

    private static void recolorChildren(java.awt.Container panel, Color fg) {
        for (java.awt.Component c : panel.getComponents()) {
            if (c instanceof JLabel l) l.setForeground(fg);
            if (c instanceof java.awt.Container child) recolorChildren(child, fg);
        }
    }
}
