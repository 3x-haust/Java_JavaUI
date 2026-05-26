package io.github._3xhaust.theme;

import java.awt.Color;

/**
 * Color manipulation helpers that mirror Flutter's color API.
 */
public final class ColorUtils {
    private ColorUtils() {}

    /** Parses {@code #RGB}, {@code #RGBA}, {@code #RRGGBB}, {@code #RRGGBBAA}. */
    public static Color fromHex(String hex) {
        if (hex == null) throw new IllegalArgumentException("hex is null");
        String s = hex.trim();
        if (s.startsWith("#")) s = s.substring(1);
        if (s.length() == 3) {
            s = "" + s.charAt(0) + s.charAt(0) + s.charAt(1) + s.charAt(1) + s.charAt(2) + s.charAt(2);
        } else if (s.length() == 4) {
            s = "" + s.charAt(0) + s.charAt(0) + s.charAt(1) + s.charAt(1)
                    + s.charAt(2) + s.charAt(2) + s.charAt(3) + s.charAt(3);
        }
        if (s.length() == 6) {
            int rgb = Integer.parseInt(s, 16);
            return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
        }
        if (s.length() == 8) {
            long rgba = Long.parseLong(s, 16);
            return new Color(
                    (int) ((rgba >> 24) & 0xFF),
                    (int) ((rgba >> 16) & 0xFF),
                    (int) ((rgba >> 8) & 0xFF),
                    (int) (rgba & 0xFF));
        }
        throw new IllegalArgumentException("Unsupported hex color: " + hex);
    }

    public static Color withAlpha(Color base, int alpha) {
        int a = clamp(alpha, 0, 255);
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), a);
    }

    public static Color withOpacity(Color base, double opacity) {
        return withAlpha(base, (int) Math.round(clamp(opacity, 0.0, 1.0) * 255));
    }

    /** Lightens by mixing toward white, amount in [0,1]. */
    public static Color lighten(Color base, double amount) {
        double t = clamp(amount, 0.0, 1.0);
        return new Color(
                lerp(base.getRed(), 255, t),
                lerp(base.getGreen(), 255, t),
                lerp(base.getBlue(), 255, t),
                base.getAlpha());
    }

    /** Darkens by mixing toward black, amount in [0,1]. */
    public static Color darken(Color base, double amount) {
        double t = clamp(amount, 0.0, 1.0);
        return new Color(
                lerp(base.getRed(), 0, t),
                lerp(base.getGreen(), 0, t),
                lerp(base.getBlue(), 0, t),
                base.getAlpha());
    }

    public static Color blend(Color a, Color b, double t) {
        double f = clamp(t, 0.0, 1.0);
        return new Color(
                lerp(a.getRed(), b.getRed(), f),
                lerp(a.getGreen(), b.getGreen(), f),
                lerp(a.getBlue(), b.getBlue(), f),
                lerp(a.getAlpha(), b.getAlpha(), f));
    }

    /** Returns a contrasting foreground color (black or white) for the given background. */
    public static Color contrasting(Color background) {
        double luminance = (0.299 * background.getRed()
                + 0.587 * background.getGreen()
                + 0.114 * background.getBlue()) / 255.0;
        return luminance > 0.55 ? Color.BLACK : Color.WHITE;
    }

    public static String toHex(Color c) {
        return String.format("#%02X%02X%02X%02X",
                c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    private static int lerp(int a, int b, double t) {
        return (int) Math.round(a + (b - a) * t);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
