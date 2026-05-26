package io.github._3xhaust.dsl.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.Theme;

/**
 * Page-level layout container. Slots:
 *
 * <ul>
 *   <li>{@link #appBar(View)} - top bar</li>
 *   <li>{@link #body(View)} - main content</li>
 *   <li>{@link #bottomBar(View)} - bottom navigation/footer</li>
 *   <li>{@link #floatingActionButton(View)} - bottom-right floating button</li>
 * </ul>
 */
public final class ScaffoldWidget extends AbstractWidget<ScaffoldWidget> {
    private View appBar;
    private View body;
    private View bottomBar;
    private View fab;
    private Color backgroundColor;

    public static ScaffoldWidget Scaffold() {
        return new ScaffoldWidget();
    }

    public ScaffoldWidget appBar(View v) { this.appBar = v; return this; }
    public ScaffoldWidget body(View v) { this.body = v; return this; }
    public ScaffoldWidget bottomBar(View v) { this.bottomBar = v; return this; }
    public ScaffoldWidget floatingActionButton(View v) { this.fab = v; return this; }
    public ScaffoldWidget backgroundColor(Color c) { this.backgroundColor = c; return this; }

    @Override
    public List<View> getChildren() {
        List<View> out = new ArrayList<>();
        if (appBar != null) out.add(appBar);
        if (body != null) out.add(body);
        if (bottomBar != null) out.add(bottomBar);
        if (fab != null) out.add(fab);
        return out;
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(backgroundColor != null ? backgroundColor : Theme.get().background());

        if (appBar != null) {
            JPanel slot = new JPanel(new BorderLayout());
            slot.setOpaque(false);
            swing.pushContainer(slot);
            appBar.render(renderer);
            swing.popContainer();
            root.add(slot, BorderLayout.NORTH);
        }

        if (body != null) {
            JPanel bodyContainer = new JPanel(new BorderLayout());
            bodyContainer.setOpaque(false);
            swing.pushContainer(bodyContainer);
            body.render(renderer);
            swing.popContainer();

            if (fab != null) {
                JPanel fabSlot = new JPanel(new GridBagLayout());
                fabSlot.setOpaque(false);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.weighty = 1;
                gbc.anchor = GridBagConstraints.SOUTHEAST;
                gbc.insets = new java.awt.Insets(0, 0, 16, 16);

                JPanel fabHost = new JPanel();
                fabHost.setOpaque(false);
                swing.pushContainer(fabHost);
                fab.render(renderer);
                swing.popContainer();
                fabSlot.add(fabHost, gbc);

                JPanel wrapper = new JPanel();
                wrapper.setOpaque(false);
                wrapper.setLayout(new OverlayLayout(wrapper));
                fabSlot.setAlignmentX(0.5f);
                fabSlot.setAlignmentY(0.5f);
                bodyContainer.setAlignmentX(0.5f);
                bodyContainer.setAlignmentY(0.5f);
                wrapper.add(fabSlot);
                wrapper.add(bodyContainer);
                root.add(wrapper, BorderLayout.CENTER);
            } else {
                root.add(bodyContainer, BorderLayout.CENTER);
            }
        }

        if (bottomBar != null) {
            JPanel slot = new JPanel(new BorderLayout());
            slot.setOpaque(false);
            swing.pushContainer(slot);
            bottomBar.render(renderer);
            swing.popContainer();
            root.add(slot, BorderLayout.SOUTH);
        }

        SwingRenderer.applyStyles(root, getStyle());
        swing.addComponent(root);
    }
}
