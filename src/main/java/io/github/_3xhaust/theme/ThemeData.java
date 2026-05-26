package io.github._3xhaust.theme;

import java.awt.Color;
import java.awt.Font;
import java.util.Objects;

import io.github._3xhaust.util.Fonts;

/**
 * Material-flavored theme data. Pass instances into {@link Theme#set(ThemeData)}
 * to apply globally; the framework will rebuild the active route.
 */
public final class ThemeData {
    private final ThemeMode mode;
    private final Color primary;
    private final Color onPrimary;
    private final Color secondary;
    private final Color onSecondary;
    private final Color background;
    private final Color surface;
    private final Color onBackground;
    private final Color onSurface;
    private final Color error;
    private final Color outline;
    private final Color hover;
    private final Color disabled;
    private final Font fontFamily;
    private final int radius;
    private final boolean dark;

    private ThemeData(Builder b) {
        this.mode = b.mode;
        this.primary = b.primary;
        this.onPrimary = b.onPrimary;
        this.secondary = b.secondary;
        this.onSecondary = b.onSecondary;
        this.background = b.background;
        this.surface = b.surface;
        this.onBackground = b.onBackground;
        this.onSurface = b.onSurface;
        this.error = b.error;
        this.outline = b.outline;
        this.hover = b.hover;
        this.disabled = b.disabled;
        this.fontFamily = b.fontFamily;
        this.radius = b.radius;
        this.dark = b.mode == ThemeMode.DARK;
    }

    public static ThemeData light() {
        return new Builder()
                .mode(ThemeMode.LIGHT)
                .primary(Colors.Blue500)
                .onPrimary(Colors.White)
                .secondary(Colors.Pink500)
                .onSecondary(Colors.White)
                .background(Colors.Grey50)
                .surface(Colors.White)
                .onBackground(Colors.Grey900)
                .onSurface(Colors.Grey900)
                .error(Colors.Red500)
                .outline(Colors.Grey300)
                .hover(new Color(0, 0, 0, 12))
                .disabled(Colors.Grey400)
                .fontFamily(Fonts.sans(14))
                .radius(8)
                .build();
    }

    public static ThemeData dark() {
        return new Builder()
                .mode(ThemeMode.DARK)
                .primary(Colors.Blue300)
                .onPrimary(Colors.Black)
                .secondary(Colors.Pink300)
                .onSecondary(Colors.Black)
                .background(new Color(18, 18, 18))
                .surface(new Color(30, 30, 30))
                .onBackground(Colors.Grey50)
                .onSurface(Colors.Grey50)
                .error(Colors.Red300)
                .outline(Colors.Grey700)
                .hover(new Color(255, 255, 255, 18))
                .disabled(Colors.Grey600)
                .fontFamily(Fonts.sans(14))
                .radius(8)
                .build();
    }

    public ThemeMode mode() { return mode; }
    public Color primary() { return primary; }
    public Color onPrimary() { return onPrimary; }
    public Color secondary() { return secondary; }
    public Color onSecondary() { return onSecondary; }
    public Color background() { return background; }
    public Color surface() { return surface; }
    public Color onBackground() { return onBackground; }
    public Color onSurface() { return onSurface; }
    public Color error() { return error; }
    public Color outline() { return outline; }
    public Color hover() { return hover; }
    public Color disabled() { return disabled; }
    public Font fontFamily() { return fontFamily; }
    public int radius() { return radius; }
    public boolean isDark() { return dark; }

    public Builder toBuilder() {
        return new Builder()
                .mode(mode).primary(primary).onPrimary(onPrimary)
                .secondary(secondary).onSecondary(onSecondary)
                .background(background).surface(surface)
                .onBackground(onBackground).onSurface(onSurface)
                .error(error).outline(outline).hover(hover)
                .disabled(disabled).fontFamily(fontFamily).radius(radius);
    }

    public static final class Builder {
        private ThemeMode mode = ThemeMode.LIGHT;
        private Color primary = Colors.Blue500;
        private Color onPrimary = Colors.White;
        private Color secondary = Colors.Pink500;
        private Color onSecondary = Colors.White;
        private Color background = Colors.Grey50;
        private Color surface = Colors.White;
        private Color onBackground = Colors.Grey900;
        private Color onSurface = Colors.Grey900;
        private Color error = Colors.Red500;
        private Color outline = Colors.Grey300;
        private Color hover = new Color(0, 0, 0, 12);
        private Color disabled = Colors.Grey400;
        private Font fontFamily = Fonts.sans(14);
        private int radius = 8;

        public Builder mode(ThemeMode v) { this.mode = Objects.requireNonNull(v); return this; }
        public Builder primary(Color v) { this.primary = v; return this; }
        public Builder onPrimary(Color v) { this.onPrimary = v; return this; }
        public Builder secondary(Color v) { this.secondary = v; return this; }
        public Builder onSecondary(Color v) { this.onSecondary = v; return this; }
        public Builder background(Color v) { this.background = v; return this; }
        public Builder surface(Color v) { this.surface = v; return this; }
        public Builder onBackground(Color v) { this.onBackground = v; return this; }
        public Builder onSurface(Color v) { this.onSurface = v; return this; }
        public Builder error(Color v) { this.error = v; return this; }
        public Builder outline(Color v) { this.outline = v; return this; }
        public Builder hover(Color v) { this.hover = v; return this; }
        public Builder disabled(Color v) { this.disabled = v; return this; }
        public Builder fontFamily(Font v) { this.fontFamily = v; return this; }
        public Builder radius(int v) { this.radius = v; return this; }
        public ThemeData build() { return new ThemeData(this); }
    }
}
