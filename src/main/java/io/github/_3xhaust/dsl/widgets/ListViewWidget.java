package io.github._3xhaust.dsl.widgets;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.platform.swing.SwingRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ListViewWidget implements View {
    private final List<View> children;
    private final boolean scrollDirection;
    private final EdgeInsets padding;

    public ListViewWidget(List<View> children) {
        this(children, true, null);
    }

    public ListViewWidget(List<View> children, boolean vertical) {
        this(children, vertical, null);
    }

    public ListViewWidget(List<View> children, boolean vertical, EdgeInsets padding) {
        this.children = children;
        this.scrollDirection = vertical;
        this.padding = padding;
    }

    public static ListViewWidget ListView(List<View> children) {
        return new ListViewWidget(children);
    }

    private static class EdgeInsets {
        final double top, right, bottom, left;

        public EdgeInsets(double top, double right, double bottom, double left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
        }

        public static EdgeInsets all(double value) {
            return new EdgeInsets(value, value, value, value);
        }

        public static EdgeInsets symmetric(double vertical, double horizontal) {
            return new EdgeInsets(vertical, horizontal, vertical, horizontal);
        }
    }

    private static class ScrollableListPanel extends JPanel implements Scrollable {
        private final boolean isVertical;

        public ScrollableListPanel(boolean isVertical) {
            this.isVertical = isVertical;
            setLayout(new BoxLayout(this, isVertical ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS));
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
            return isVertical && orientation == SwingConstants.VERTICAL ? visibleRect.height - 16 :
                   !isVertical && orientation == SwingConstants.HORIZONTAL ? visibleRect.width - 16 : 16;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return isVertical;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return !isVertical;
        }
    }

    @Override
    public void render(Renderer renderer) {
        if (renderer instanceof SwingRenderer swing) {
            ScrollableListPanel listPanel = new ScrollableListPanel(scrollDirection);
            listPanel.setOpaque(false);

            if (padding != null) {
                listPanel.setBorder(BorderFactory.createEmptyBorder(
                    (int) padding.top, (int) padding.left, (int) padding.bottom, (int) padding.right));
            }

            swing.pushContainer(listPanel);
            for (View child : children) {
                child.render(swing);
            }
            swing.popContainer();

            JScrollPane scrollPane = new JScrollPane(listPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setOpaque(false);

            if (scrollDirection) {
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            } else {
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
            }

            swing.addComponent(scrollPane);
        }
    }

    public static class Builder {
        private List<View> children;
        private boolean vertical = true;
        private EdgeInsets padding;

        public Builder children(List<View> children) {
            this.children = children;
            return this;
        }

        public Builder vertical(boolean vertical) {
            this.vertical = vertical;
            return this;
        }

        public Builder padding(EdgeInsets padding) {
            this.padding = padding;
            return this;
        }

        public ListViewWidget build() {
            return new ListViewWidget(children, vertical, padding);
        }
    }
}
