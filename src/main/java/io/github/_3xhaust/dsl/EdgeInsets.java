package io.github._3xhaust.dsl;

public class EdgeInsets {
    public final double top;
    public final double right;
    public final double bottom;
    public final double left;

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

    public static EdgeInsets only(double top, double right, double bottom, double left) {
        return new EdgeInsets(top, right, bottom, left);
    }

    public static EdgeInsets fromLTRB(double left, double top, double right, double bottom) {
        return new EdgeInsets(top, right, bottom, left);
    }

    public static EdgeInsets zero() {
        return new EdgeInsets(0, 0, 0, 0);
    }

    public java.awt.Insets toAwtInsets() {
        return new java.awt.Insets((int) top, (int) left, (int) bottom, (int) right);
    }
}
