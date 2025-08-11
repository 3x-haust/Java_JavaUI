package io.github._3xhaust.dsl;

import java.awt.*;

public class Style {
    private Font font;
    private Color foregroundColor;
    private Color backgroundColor;
    private Dimension size;
    private Dimension minimumSize;
    private Dimension maximumSize;
    private Dimension preferredSize;
    private int horizontalAlignment = -1;
    private int verticalAlignment = -1;
    private Insets padding;
    private boolean opaque;
    private Border border;

    public static class Border {
        private final int thickness;
        private final Color color;
        private final boolean raised;

        public Border(int thickness, Color color) {
            this(thickness, color, false);
        }

        public Border(int thickness, Color color, boolean raised) {
            this.thickness = thickness;
            this.color = color;
            this.raised = raised;
        }

        public int getThickness() { return thickness; }
        public Color getColor() { return color; }
        public boolean isRaised() { return raised; }
    }

    public Style() {}

    public Style font(String name, int style, int size) {
        this.font = new Font(name, style, size);
        return this;
    }

    public Style font(Font font) {
        this.font = font;
        return this;
    }

    public Style fontSize(int size) {
        if (this.font != null) {
            this.font = this.font.deriveFont((float) size);
        } else {
            this.font = new Font(Font.SANS_SERIF, Font.PLAIN, size);
        }
        return this;
    }

    public Style fontStyle(int style) {
        if (this.font != null) {
            this.font = this.font.deriveFont(style);
        } else {
            this.font = new Font(Font.SANS_SERIF, style, 12);
        }
        return this;
    }

    public Style bold() {
        return fontStyle(Font.BOLD);
    }

    public Style italic() {
        return fontStyle(Font.ITALIC);
    }

    public Style color(Color color) {
        this.foregroundColor = color;
        return this;
    }

    public Style color(int r, int g, int b) {
        return color(new Color(r, g, b));
    }

    public Style backgroundColor(Color color) {
        this.backgroundColor = color;
        this.opaque = true;
        return this;
    }

    public Style backgroundColor(int r, int g, int b) {
        return backgroundColor(new Color(r, g, b));
    }

    public Style size(int width, int height) {
        this.size = new Dimension(width, height);
        return this;
    }

    public Style width(int width) {
        if (this.size == null) {
            this.size = new Dimension(width, 0);
        } else {
            this.size.width = width;
        }
        return this;
    }

    public Style height(int height) {
        if (this.size == null) {
            this.size = new Dimension(0, height);
        } else {
            this.size.height = height;
        }
        return this;
    }

    public Style preferredSize(int width, int height) {
        this.preferredSize = new Dimension(width, height);
        return this;
    }

    public Style minimumSize(int width, int height) {
        this.minimumSize = new Dimension(width, height);
        return this;
    }

    public Style maximumSize(int width, int height) {
        this.maximumSize = new Dimension(width, height);
        return this;
    }

    public Style padding(int top, int left, int bottom, int right) {
        this.padding = new Insets(top, left, bottom, right);
        return this;
    }

    public Style padding(int all) {
        return padding(all, all, all, all);
    }

    public Style border(int thickness, Color color) {
        this.border = new Border(thickness, color);
        return this;
    }

    public Style border(int thickness, Color color, boolean raised) {
        this.border = new Border(thickness, color, raised);
        return this;
    }

    public Style alignLeft() {
        this.horizontalAlignment = javax.swing.SwingConstants.LEFT;
        return this;
    }

    public Style alignCenter() {
        this.horizontalAlignment = javax.swing.SwingConstants.CENTER;
        return this;
    }

    public Style alignRight() {
        this.horizontalAlignment = javax.swing.SwingConstants.RIGHT;
        return this;
    }

    public Style alignTop() {
        this.verticalAlignment = javax.swing.SwingConstants.TOP;
        return this;
    }

    public Style alignMiddle() {
        this.verticalAlignment = javax.swing.SwingConstants.CENTER;
        return this;
    }

    public Style alignBottom() {
        this.verticalAlignment = javax.swing.SwingConstants.BOTTOM;
        return this;
    }

    public Font getFont() { return font; }
    public Color getForegroundColor() { return foregroundColor; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Dimension getSize() { return size; }
    public Dimension getMinimumSize() { return minimumSize; }
    public Dimension getMaximumSize() { return maximumSize; }
    public Dimension getPreferredSize() { return preferredSize; }
    public int getHorizontalAlignment() { return horizontalAlignment; }
    public int getVerticalAlignment() { return verticalAlignment; }
    public Insets getPadding() { return padding; }
    public boolean isOpaque() { return opaque; }
    public Border getBorder() { return border; }
}
