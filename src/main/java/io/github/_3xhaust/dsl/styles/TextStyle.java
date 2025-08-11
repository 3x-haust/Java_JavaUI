package io.github._3xhaust.dsl.styles;

import java.awt.*;

public class TextStyle {
    private Font font;
    private Color color;
    private Color backgroundColor;
    private int fontSize = -1;
    private int fontStyle = -1;
    private String fontFamily;
    private int textAlign = -1;
    private Insets padding;
    private BorderStyle border;

    public TextStyle() {}

    public TextStyle fontSize(int size) {
        this.fontSize = size;
        return this;
    }

    public TextStyle color(Color color) {
        this.color = color;
        return this;
    }

    public TextStyle color(String colorStr) {
        this.color = parseColor(colorStr);
        return this;
    }

    public TextStyle backgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public TextStyle backgroundColor(String colorStr) {
        this.backgroundColor = parseColor(colorStr);
        return this;
    }

    public TextStyle fontFamily(String family) {
        this.fontFamily = family;
        return this;
    }

    public TextStyle fontWeight(String weight) {
        switch (weight.toLowerCase()) {
            case "bold" -> this.fontStyle = Font.BOLD;
            case "italic" -> this.fontStyle = Font.ITALIC;
            case "normal" -> this.fontStyle = Font.PLAIN;
        }
        return this;
    }

    public TextStyle textAlign(String align) {
        switch (align.toLowerCase()) {
            case "left" -> this.textAlign = javax.swing.SwingConstants.LEFT;
            case "center" -> this.textAlign = javax.swing.SwingConstants.CENTER;
            case "right" -> this.textAlign = javax.swing.SwingConstants.RIGHT;
        }
        return this;
    }

    public TextStyle padding(int all) {
        this.padding = new Insets(all, all, all, all);
        return this;
    }

    public TextStyle padding(int vertical, int horizontal) {
        this.padding = new Insets(vertical, horizontal, vertical, horizontal);
        return this;
    }

    public TextStyle border(BorderStyle border) {
        this.border = border;
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
    public int getTextAlign() { return textAlign; }
    public Insets getPadding() { return padding; }
    public BorderStyle getBorder() { return border; }
}
