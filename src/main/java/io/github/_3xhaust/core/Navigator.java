package io.github._3xhaust.core;

/**
 * Static facade over the active {@link JavaUIApp}'s {@link Router}.
 * Provides Flutter-like one-line navigation: {@code Navigator.to("/home");}
 */
public final class Navigator {
    private Navigator() {}

    public static void to(String route) {
        JavaUIApp app = JavaUIApp.getInstance();
        if (app == null) {
            io.github._3xhaust.util.Logger.get(Navigator.class)
                    .warn("JavaUIApp not initialized; ignoring navigate to " + route);
            return;
        }
        app.navigateTo(route);
    }

    public static void back() {
        JavaUIApp app = JavaUIApp.getInstance();
        if (app != null) app.back();
    }

    public static void forward() {
        JavaUIApp app = JavaUIApp.getInstance();
        if (app != null) app.forward();
    }

    public static String currentPath() {
        JavaUIApp app = JavaUIApp.getInstance();
        return app == null ? null : app.router().currentPath();
    }

    public static RouteParams params() {
        JavaUIApp app = JavaUIApp.getInstance();
        return app == null ? RouteParams.empty() : app.router().currentParams();
    }
}
