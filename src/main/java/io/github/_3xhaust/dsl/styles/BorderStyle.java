package io.github._3xhaust.dsl.styles;

import java.awt.*;

public class BorderStyle {
    private int width;
    private Color color;
    private String style;

    public BorderStyle() {}

    public BorderStyle width(int width) {
        this.width = width;
        return this;
    }

    public BorderStyle color(Color color) {
        this.color = color;
        return this;
    }

    public BorderStyle color(String colorStr) {
        this.color = parseColor(colorStr);
        return this;
    }

    public BorderStyle style(String style) {
        this.style = style;
        return this;
    }

    public static BorderStyle all(int width, String style, Color color) {
        return new BorderStyle().width(width).style(style).color(color);
    }

    public static BorderStyle all(int width, String style, String color) {
        return new BorderStyle().width(width).style(style).color(color);
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

    public int getWidth() { return width; }
    public Color getColor() { return color; }
    public String getStyle() { return style; }
}
