package io.github._3xhaust.dsl.widgets;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.platform.swing.SwingRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GridViewWidget implements View {
    private final List<View> children;
    private final int crossAxisCount;
    private final double mainAxisSpacing;
    private final double crossAxisSpacing;
    private final double childAspectRatio;
    private final EdgeInsets padding;

    public GridViewWidget(List<View> children, int crossAxisCount) {
        this(children, crossAxisCount, 0.0, 0.0, 1.0, null);
    }

    public static GridViewWidget GridView(List<View> children) {
        return new GridViewWidget(children, 2);
    }

    public GridViewWidget(List<View> children, int crossAxisCount, double mainAxisSpacing,
                         double crossAxisSpacing, double childAspectRatio, EdgeInsets padding) {
        this.children = children;
        this.crossAxisCount = crossAxisCount;
        this.mainAxisSpacing = mainAxisSpacing;
        this.crossAxisSpacing = crossAxisSpacing;
        this.childAspectRatio = childAspectRatio;
        this.padding = padding;
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

    private static class GridPanel extends JPanel implements Scrollable {
        private final int columns;
        private final double aspectRatio;
        private final int hgap;
        private final int vgap;

        public GridPanel(int columns, double aspectRatio, int hgap, int vgap) {
            this.columns = columns;
            this.aspectRatio = aspectRatio;
            this.hgap = hgap;
            this.vgap = vgap;
            setLayout(new GridLayout(0, columns, hgap, vgap));
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
            return visibleRect.height - 16;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }

        @Override
        public Dimension getPreferredSize() {
            int componentCount = getComponentCount();
            if (componentCount == 0) return new Dimension(0, 0);

            Component parent = getParent();
            if (parent == null) return super.getPreferredSize();

            int parentWidth = parent.getWidth();
            if (parentWidth <= 0) return super.getPreferredSize();

            int cellWidth = (parentWidth - (columns - 1) * hgap) / columns;
            int cellHeight = (int) (cellWidth / aspectRatio);
            int rows = (componentCount + columns - 1) / columns;

            return new Dimension(parentWidth, rows * cellHeight + (rows - 1) * vgap);
        }
    }

    @Override
    public void render(Renderer renderer) {
        if (renderer instanceof SwingRenderer swing) {
            GridPanel gridPanel = new GridPanel(crossAxisCount, childAspectRatio,
                                              (int) crossAxisSpacing, (int) mainAxisSpacing);
            gridPanel.setOpaque(false);

            if (padding != null) {
                gridPanel.setBorder(BorderFactory.createEmptyBorder(
                    (int) padding.top, (int) padding.left, (int) padding.bottom, (int) padding.right));
            }

            for (View child : children) {
                JPanel cellWrapper = new JPanel(new BorderLayout());
                cellWrapper.setOpaque(false);
                swing.pushContainer(cellWrapper);
                child.render(swing);
                swing.popContainer();
                gridPanel.add(cellWrapper);
            }

            JScrollPane scrollPane = new JScrollPane(gridPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getViewport().setOpaque(false);

            swing.addComponent(scrollPane);
        }
    }

    public static class Builder {
        private List<View> children;
        private int crossAxisCount = 2;
        private double mainAxisSpacing = 0.0;
        private double crossAxisSpacing = 0.0;
        private double childAspectRatio = 1.0;
        private EdgeInsets padding;

        public Builder children(List<View> children) {
            this.children = children;
            return this;
        }

        public Builder crossAxisCount(int count) {
            this.crossAxisCount = count;
            return this;
        }

        public Builder mainAxisSpacing(double spacing) {
            this.mainAxisSpacing = spacing;
            return this;
        }

        public Builder crossAxisSpacing(double spacing) {
            this.crossAxisSpacing = spacing;
            return this;
        }

        public Builder childAspectRatio(double ratio) {
            this.childAspectRatio = ratio;
            return this;
        }

        public Builder padding(EdgeInsets padding) {
            this.padding = padding;
            return this;
        }

        public GridViewWidget build() {
            return new GridViewWidget(children, crossAxisCount, mainAxisSpacing,
                                    crossAxisSpacing, childAspectRatio, padding);
        }
    }
}
