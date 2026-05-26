package io.github._3xhaust.core;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * URL-style router with path parameters, history stack and route guards.
 *
 * <ul>
 *   <li>Supports static segments: {@code /home}</li>
 *   <li>Path params: {@code /users/:id} → matched against {@code /users/42}</li>
 *   <li>Catch-all wildcard: {@code /*} matches anything not previously matched.</li>
 * </ul>
 */
public final class Router {
    private final Map<String, Function<RouteParams, View>> routes = new LinkedHashMap<>();
    private final Deque<HistoryEntry> backStack = new ArrayDeque<>();
    private final Deque<HistoryEntry> forwardStack = new ArrayDeque<>();
    private final List<Predicate<String>> guards = new CopyOnWriteArrayList<>();
    private final List<RouteListener> listeners = new CopyOnWriteArrayList<>();
    private HistoryEntry current;

    public Router register(String pattern, Supplier<View> builder) {
        return register(pattern, params -> builder.get());
    }

    public Router register(String pattern, Function<RouteParams, View> builder) {
        if (pattern == null || builder == null) {
            throw new IllegalArgumentException("pattern/builder must not be null");
        }
        routes.put(pattern, builder);
        return this;
    }

    public Router guard(Predicate<String> guard) {
        guards.add(guard);
        return this;
    }

    public Router onChange(RouteListener listener) {
        listeners.add(listener);
        return this;
    }

    public boolean canGoBack() { return !backStack.isEmpty(); }
    public boolean canGoForward() { return !forwardStack.isEmpty(); }

    public String currentPath() {
        return current == null ? null : current.path;
    }

    public RouteParams currentParams() {
        return current == null ? RouteParams.empty() : current.params;
    }

    public Map<String, Function<RouteParams, View>> routes() {
        return Collections.unmodifiableMap(routes);
    }

    public View resolve(String path) {
        Match m = match(path);
        if (m == null) return null;
        return m.builder.apply(m.params);
    }

    public View navigate(String path) {
        Match m = match(path);
        if (m == null) {
            io.github._3xhaust.util.Logger.get(Router.class).warn("Route not found: " + path);
            return null;
        }
        for (Predicate<String> g : guards) {
            if (!g.test(path)) {
                io.github._3xhaust.util.Logger.get(Router.class).warn("Route blocked by guard: " + path);
                return null;
            }
        }
        if (current != null) backStack.push(current);
        forwardStack.clear();
        current = new HistoryEntry(path, m.params);
        notify(path, m.params);
        return m.builder.apply(m.params);
    }

    public View back() {
        if (backStack.isEmpty()) return null;
        if (current != null) forwardStack.push(current);
        current = backStack.pop();
        Match m = match(current.path);
        if (m == null) return null;
        notify(current.path, current.params);
        return m.builder.apply(current.params);
    }

    public View forward() {
        if (forwardStack.isEmpty()) return null;
        if (current != null) backStack.push(current);
        current = forwardStack.pop();
        Match m = match(current.path);
        if (m == null) return null;
        notify(current.path, current.params);
        return m.builder.apply(current.params);
    }

    private void notify(String path, RouteParams params) {
        for (RouteListener l : listeners) {
            try { l.onChange(path, params); } catch (RuntimeException ignored) { /* fault tolerant */ }
        }
    }

    private Match match(String path) {
        if (path == null) return null;
        // exact match short-circuit
        Function<RouteParams, View> exact = routes.get(path);
        if (exact != null) return new Match(exact, RouteParams.empty());
        // pattern matching
        String[] reqSegments = splitSegments(path);
        for (var entry : routes.entrySet()) {
            String pattern = entry.getKey();
            String[] patSegments = splitSegments(pattern);
            Map<String, String> params = matchPattern(patSegments, reqSegments);
            if (params != null) return new Match(entry.getValue(), new RouteParams(params));
        }
        // wildcard fallback
        Function<RouteParams, View> wildcard = routes.get("/*");
        if (wildcard != null) return new Match(wildcard, RouteParams.empty());
        return null;
    }

    private static String[] splitSegments(String path) {
        String trimmed = path;
        if (trimmed.startsWith("/")) trimmed = trimmed.substring(1);
        if (trimmed.endsWith("/") && trimmed.length() > 1) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.isEmpty()) return new String[0];
        return trimmed.split("/");
    }

    private static Map<String, String> matchPattern(String[] pattern, String[] request) {
        if (pattern.length != request.length) return null;
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < pattern.length; i++) {
            String p = pattern[i];
            String r = request[i];
            if (p.startsWith(":")) {
                params.put(p.substring(1), r);
            } else if (p.equals("*")) {
                // single-segment wildcard
            } else if (!p.equals(r)) {
                return null;
            }
        }
        return params;
    }

    @FunctionalInterface
    public interface RouteListener {
        void onChange(String path, RouteParams params);
    }

    private static final class HistoryEntry {
        final String path;
        final RouteParams params;

        HistoryEntry(String path, RouteParams params) {
            this.path = path;
            this.params = Objects.requireNonNullElse(params, RouteParams.empty());
        }
    }

    private static final class Match {
        final Function<RouteParams, View> builder;
        final RouteParams params;

        Match(Function<RouteParams, View> b, RouteParams p) {
            this.builder = b;
            this.params = p;
        }
    }
}
