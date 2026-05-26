package io.github._3xhaust.dsl.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;

/**
 * Tabbed page container. Each tab pairs a label with a builder that produces
 * the tab's content lazily.
 */
public final class TabsWidget extends AbstractWidget<TabsWidget> {
    public record Tab(String title, View content) {}

    private final List<Tab> tabs = new ArrayList<>();

    public static TabsWidget Tabs() {
        return new TabsWidget();
    }

    public TabsWidget tab(String title, View content) {
        tabs.add(new Tab(title, content));
        return this;
    }

    @Override
    public List<View> getChildren() {
        List<View> out = new ArrayList<>();
        for (Tab t : tabs) if (t.content != null) out.add(t.content);
        return out;
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JTabbedPane pane = new JTabbedPane();
        SwingRenderer.applyStyles(pane, getStyle());
        for (Tab t : tabs) {
            JPanel page = new JPanel(new java.awt.BorderLayout());
            page.setOpaque(false);
            if (t.content != null) {
                swing.pushContainer(page);
                t.content.render(renderer);
                swing.popContainer();
            }
            pane.addTab(t.title, page);
        }
        swing.addComponent(pane);
    }
}
