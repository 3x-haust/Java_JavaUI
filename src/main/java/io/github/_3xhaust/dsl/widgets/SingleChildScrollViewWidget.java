package io.github._3xhaust.dsl.widgets;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.platform.swing.SwingRenderer;

import javax.swing.*;
import java.awt.*;

public class SingleChildScrollViewWidget implements View {
    private final View child;

    public SingleChildScrollViewWidget(View child) {
        this.child = child;
    }

    public static SingleChildScrollViewWidget SingleChildScrollView(View child) {
        return new SingleChildScrollViewWidget(child);
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
        public ScrollablePanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    @Override
    public void render(Renderer renderer) {
        if (renderer instanceof SwingRenderer swing) {
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setOpaque(false);

            swing.pushContainer(contentPanel);
            child.render(swing);
            swing.popContainer();

            JPanel wrapperPanel = new ScrollablePanel(new BorderLayout());
            wrapperPanel.add(contentPanel, BorderLayout.NORTH);
            wrapperPanel.setOpaque(false);

            JScrollPane scrollPane = new JScrollPane(wrapperPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getViewport().setOpaque(false);

            swing.addComponent(scrollPane);
        }
    }
}