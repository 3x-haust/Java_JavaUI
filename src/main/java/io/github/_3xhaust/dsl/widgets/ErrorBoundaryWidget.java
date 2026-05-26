package io.github._3xhaust.dsl.widgets;

import java.util.List;
import java.util.function.Function;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.util.Logger;

/**
 * Catches exceptions thrown during a child's render and substitutes a fallback
 * view so a single broken widget doesn't crash the whole UI.
 */
public final class ErrorBoundaryWidget extends AbstractWidget<ErrorBoundaryWidget> {
    private static final Logger LOG = Logger.get(ErrorBoundaryWidget.class);

    private final View child;
    private final Function<Throwable, View> fallback;

    public ErrorBoundaryWidget(View child, Function<Throwable, View> fallback) {
        this.child = child;
        this.fallback = fallback;
    }

    public static ErrorBoundaryWidget ErrorBoundary(View child, Function<Throwable, View> fallback) {
        return new ErrorBoundaryWidget(child, fallback);
    }

    public static ErrorBoundaryWidget ErrorBoundary(View child) {
        return new ErrorBoundaryWidget(child, t -> TextWidget.Text("Error: " + t.getMessage()));
    }

    @Override
    public List<View> getChildren() {
        return child != null ? List.of(child) : List.of();
    }

    @Override
    public void render(Renderer renderer) {
        try {
            if (child != null) child.render(renderer);
        } catch (RuntimeException e) {
            LOG.error("ErrorBoundary caught: " + e.getMessage(), e);
            try {
                View f = fallback.apply(e);
                if (f != null) f.render(renderer);
            } catch (RuntimeException ignored) {
                // last resort: swallow
            }
        }
    }
}
