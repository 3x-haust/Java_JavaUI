package io.github._3xhaust.core;

import java.util.ArrayList;
import java.util.List;

import io.github._3xhaust.state.State;

/**
 * A widget that owns mutable state and rebuilds when {@link #setState(Runnable)} is
 * invoked. Override {@link #initState()} to do one-time setup, {@link #build()} to
 * describe the UI, and {@link #dispose()} to clean up resources.
 *
 * <p>Calling {@code setState(...)} schedules a rebuild on the next render frame
 * via {@link JavaUIApp#requestRebuild()}, so listeners can chain mutations without
 * worrying about render storms.</p>
 */
public abstract class StatefulWidget implements View {
    private boolean initialized;
    private final List<AutoCloseable> subscriptions = new ArrayList<>();

    /** Lifecycle hook fired exactly once before the first build. */
    public void initState() {}

    /** Lifecycle hook fired when this widget is removed from the tree. */
    public void dispose() {
        for (AutoCloseable c : subscriptions) {
            try { c.close(); } catch (Exception ignored) { /* best effort */ }
        }
        subscriptions.clear();
    }

    /** Subscribe to a {@link State} so this widget rebuilds whenever the state changes. */
    protected <T> void watch(State<T> state) {
        subscriptions.add(state.subscribe(v -> requestRebuild()));
    }

    /**
     * Convenience for an in-place mutation that should trigger a rebuild.
     * Mirrors Flutter's {@code setState}.
     */
    protected void setState(Runnable mutator) {
        if (mutator != null) mutator.run();
        requestRebuild();
    }

    protected void requestRebuild() {
        JavaUIApp app = JavaUIApp.getInstance();
        if (app != null) app.requestRebuild();
    }

    public abstract View build();

    @Override
    public final void render(Renderer renderer) {
        if (!initialized) {
            initialized = true;
            try {
                initState();
            } catch (RuntimeException e) {
                io.github._3xhaust.util.Logger.get(getClass())
                        .warn("initState threw: " + e.getMessage(), e);
            }
        }
        View built = build();
        if (built == null) return;
        built.render(renderer);
    }
}
