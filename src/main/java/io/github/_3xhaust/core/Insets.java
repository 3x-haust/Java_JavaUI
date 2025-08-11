package io.github._3xhaust.core;

/**
 * Platform-neutral insets (top, left, bottom, right).
 */
public class Insets {
    public final int top;
    public final int left;
    public final int bottom;
    public final int right;

    public Insets(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public static Insets all(int value) {
        return new Insets(value, value, value, value);
    }

    public static Insets verticalHorizontal(int vertical, int horizontal) {
        return new Insets(vertical, horizontal, vertical, horizontal);
    }
}


