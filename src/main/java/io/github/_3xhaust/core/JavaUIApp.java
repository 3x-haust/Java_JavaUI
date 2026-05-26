package io.github._3xhaust.core;

import java.util.Map;
import java.util.function.Supplier;

import javax.swing.JFrame;

import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.theme.ThemeData;
import io.github._3xhaust.util.Logger;

/**
 * The application root. Owns the renderer, the {@link Router} and the active view.
 *
 * <p>Typical usage:
 * <pre>
 *   JavaUIApp app = new JavaUIApp.Builder()
 *           .title("My App")
 *           .size(900, 600)
 *           .route("/", () -> new HomePage())
 *           .route("/users/:id", params -> new UserPage(params.getInt("id", 0)))
 *           .build();
 *   app.run();
 * </pre>
 */
public final class JavaUIApp {
    private static final Logger LOG = Logger.get(JavaUIApp.class);
    private static volatile JavaUIApp instance;

    private final SwingRenderer renderer;
    private final String title;
    private final int width;
    private final int height;
    private final String initialRoute;
    private final Router router;
    private final ThemeData theme;
    private View currentView;
    private boolean started;

    private JavaUIApp(Builder b) {
        this.renderer = new SwingRenderer();
        this.title = b.title;
        this.width = b.width;
        this.height = b.height;
        this.initialRoute = b.initialRoute;
        this.router = b.router;
        this.theme = b.theme;
        if (b.theme != null) Theme.set(b.theme);
        instance = this;
        Theme.subscribe(t -> requestRebuild());
    }

    /** @deprecated use {@link Builder}. Retained for backward compatibility. */
    @Deprecated
    public JavaUIApp(String initialRoute, Map<String, Supplier<View>> routes) {
        this.renderer = new SwingRenderer();
        this.title = "JavaUI App";
        this.width = 800;
        this.height = 600;
        this.initialRoute = initialRoute;
        Router r = new Router();
        if (routes != null) routes.forEach(r::register);
        this.router = r;
        this.theme = null;
        instance = this;
        Theme.subscribe(t -> requestRebuild());
    }

    public static JavaUIApp getInstance() {
        return instance;
    }

    public Router router() {
        return router;
    }

    public ThemeData theme() {
        return theme != null ? theme : Theme.get();
    }

    public JFrame getFrame() {
        return renderer.getFrame();
    }

    public SwingRenderer renderer() {
        return renderer;
    }

    public void run(View view) {
        renderer.init(title, width, height);
        currentView = view;
        renderer.mount(view);
        started = true;
    }

    public void run() {
        if (router == null || router.routes().isEmpty()) {
            throw new IllegalStateException("Router has no routes; call run(View) instead.");
        }
        renderer.init(title, width, height);
        navigateTo(initialRoute != null ? initialRoute : "/");
        started = true;
    }

    public void navigateTo(String path) {
        if (router == null) {
            LOG.warn("Router not configured");
            return;
        }
        View v = router.navigate(path);
        if (v == null) return;
        currentView = v;
        renderer.mount(v);
    }

    public void back() {
        if (router == null) return;
        View v = router.back();
        if (v == null) return;
        currentView = v;
        renderer.mount(v);
    }

    public void forward() {
        if (router == null) return;
        View v = router.forward();
        if (v == null) return;
        currentView = v;
        renderer.mount(v);
    }

    /**
     * Rebuilds the current view tree. Coalesces multiple calls within a single frame
     * via {@link SwingRenderer#requestRebuild()}.
     */
    public void requestRebuild() {
        if (!started || currentView == null) return;
        renderer.requestRebuild();
    }

    /** Internal helper used by the renderer's coalesced rebuild dispatch. */
    public View currentView() {
        return currentView;
    }

    public static final class Builder {
        private String title = "JavaUI App";
        private int width = 800;
        private int height = 600;
        private String initialRoute = "/";
        private final Router router = new Router();
        private ThemeData theme;

        public Builder title(String v) { this.title = v; return this; }
        public Builder size(int w, int h) { this.width = w; this.height = h; return this; }
        public Builder initialRoute(String v) { this.initialRoute = v; return this; }
        public Builder theme(ThemeData v) { this.theme = v; return this; }

        public Builder route(String pattern, Supplier<View> builder) {
            router.register(pattern, builder);
            return this;
        }

        public Builder route(String pattern, java.util.function.Function<RouteParams, View> builder) {
            router.register(pattern, builder);
            return this;
        }

        public Builder configureRouter(java.util.function.Consumer<Router> cfg) {
            cfg.accept(router);
            return this;
        }

        public JavaUIApp build() {
            return new JavaUIApp(this);
        }
    }
}
