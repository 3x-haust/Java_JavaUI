package io.github._3xhaust.dsl.styles;

import java.awt.*;

public class ButtonStyle {
    private Font font;
    private Color color;
    private Color backgroundColor;
    private int fontSize = -1;
    private int fontStyle = -1;
    private String fontFamily;
    private Dimension size;
    private Dimension preferredSize;
    private Insets padding;
    private BorderStyle border;
    private int textAlign = -1;

    public ButtonStyle() {}

    public ButtonStyle fontSize(int size) {
        this.fontSize = size;
        return this;
    }

    public ButtonStyle color(Color color) {
        this.color = color;
        return this;
    }

    public ButtonStyle color(String colorStr) {
        this.color = parseColor(colorStr);
        return this;
    }

    public ButtonStyle backgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public ButtonStyle backgroundColor(String colorStr) {
        this.backgroundColor = parseColor(colorStr);
        return this;
    }

    public ButtonStyle fontFamily(String family) {
        this.fontFamily = family;
        return this;
    }

    public ButtonStyle fontWeight(String weight) {
        switch (weight.toLowerCase()) {
            case "bold" -> this.fontStyle = Font.BOLD;
            case "italic" -> this.fontStyle = Font.ITALIC;
            case "normal" -> this.fontStyle = Font.PLAIN;
        }
        return this;
    }

    public ButtonStyle width(int width) {
        if (this.size == null) {
            this.size = new Dimension(width, 0);
        } else {
            this.size.width = width;
        }
        return this;
    }

    public ButtonStyle height(int height) {
        if (this.size == null) {
            this.size = new Dimension(0, height);
        } else {
            this.size.height = height;
        }
        return this;
    }

    public ButtonStyle size(int width, int height) {
        this.size = new Dimension(width, height);
        return this;
    }

    public ButtonStyle preferredSize(int width, int height) {
        this.preferredSize = new Dimension(width, height);
        return this;
    }

    public ButtonStyle padding(int all) {
        this.padding = new Insets(all, all, all, all);
        return this;
    }

    public ButtonStyle padding(int vertical, int horizontal) {
        this.padding = new Insets(vertical, horizontal, vertical, horizontal);
        return this;
    }

    public ButtonStyle border(BorderStyle border) {
        this.border = border;
        return this;
    }

    public ButtonStyle textAlign(String align) {
        switch (align.toLowerCase()) {
            case "left" -> this.textAlign = javax.swing.SwingConstants.LEFT;
            case "center" -> this.textAlign = javax.swing.SwingConstants.CENTER;
            case "right" -> this.textAlign = javax.swing.SwingConstants.RIGHT;
        }
        return this;
    }

    private Color parseColor(String colorStr) {
        return switch (colorStr.toLowerCase()) {
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            case "green" -> Color.GREEN;
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            case "gray" -> Color.GRAY;
            case "lightgray" -> Color.LIGHT_GRAY;
            case "darkgray" -> Color.DARK_GRAY;
            case "yellow" -> Color.YELLOW;
            case "orange" -> Color.ORANGE;
            case "pink" -> Color.PINK;
            case "cyan" -> Color.CYAN;
            case "magenta" -> Color.MAGENTA;
            default -> Color.BLACK;
        };
    }

    public Font getFont() {
        if (fontSize != -1 || fontStyle != -1 || fontFamily != null) {
            String family = fontFamily != null ? fontFamily : Font.SANS_SERIF;
            int style = fontStyle != -1 ? fontStyle : Font.PLAIN;
            int size = fontSize != -1 ? fontSize : 12;
            return new Font(family, style, size);
        }
        return font;
    }

    public Color getColor() { return color; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Dimension getSize() { return size; }
    public Dimension getPreferredSize() { return preferredSize; }
    public Insets getPadding() { return padding; }
    public BorderStyle getBorder() { return border; }
    public int getTextAlign() { return textAlign; }
}
