package io.github._3xhaust.theme;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Holds the active {@link ThemeData} and notifies listeners on change.
 * The framework triggers an app-level rebuild on theme change.
 */
public final class Theme {
    private static volatile ThemeData active = ThemeData.light();
    private static final List<Consumer<ThemeData>> LISTENERS = new CopyOnWriteArrayList<>();

    private Theme() {}

    public static ThemeData get() {
        return active;
    }

    public static void set(ThemeData theme) {
        if (theme == null) throw new IllegalArgumentException("theme is null");
        active = theme;
        for (Consumer<ThemeData> l : LISTENERS) {
            try { l.accept(theme); } catch (Throwable ignored) { /* fault tolerant */ }
        }
    }

    public static void toggleDark() {
        set(active.isDark() ? ThemeData.light() : ThemeData.dark());
    }

    public static AutoCloseable subscribe(Consumer<ThemeData> listener) {
        LISTENERS.add(listener);
        return () -> LISTENERS.remove(listener);
    }

    public static List<Consumer<ThemeData>> snapshot() {
        return new ArrayList<>(LISTENERS);
    }
}
