package io.github._3xhaust.theme;

import java.awt.Dimension;

import io.github._3xhaust.core.JavaUIApp;

/**
 * Provides window-size and breakpoint info similar to Flutter's MediaQuery.
 * Values are read live from the active {@link JavaUIApp} window.
 */
public final class MediaQuery {
    public static final int BREAKPOINT_SM = 600;
    public static final int BREAKPOINT_MD = 900;
    public static final int BREAKPOINT_LG = 1200;

    public enum Breakpoint { XS, SM, MD, LG, XL }

    private MediaQuery() {}

    public static Dimension size() {
        JavaUIApp app = JavaUIApp.getInstance();
        if (app == null || app.getFrame() == null) return new Dimension(0, 0);
        return app.getFrame().getContentPane().getSize();
    }

    public static int width() { return size().width; }
    public static int height() { return size().height; }

    public static Breakpoint breakpoint() {
        int w = width();
        if (w < BREAKPOINT_SM) return Breakpoint.XS;
        if (w < BREAKPOINT_MD) return Breakpoint.SM;
        if (w < BREAKPOINT_LG) return Breakpoint.MD;
        if (w < 1600) return Breakpoint.LG;
        return Breakpoint.XL;
    }

    public static boolean isMobile() { return width() < BREAKPOINT_SM; }
    public static boolean isTablet() {
        int w = width();
        return w >= BREAKPOINT_SM && w < BREAKPOINT_LG;
    }
    public static boolean isDesktop() { return width() >= BREAKPOINT_LG; }
}
