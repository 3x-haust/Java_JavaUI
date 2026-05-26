package io.github._3xhaust.core;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable bag of route parameters parsed from a path pattern.
 *
 * <pre>
 *   router.register("/users/:id", params -> new UserPage(params.get("id")));
 * </pre>
 */
public final class RouteParams {
    private static final RouteParams EMPTY = new RouteParams(Collections.emptyMap());

    private final Map<String, String> params;

    public RouteParams(Map<String, String> params) {
        this.params = Map.copyOf(params);
    }

    public static RouteParams empty() {
        return EMPTY;
    }

    public String get(String name) {
        return params.get(name);
    }

    public String getOrDefault(String name, String fallback) {
        return params.getOrDefault(name, fallback);
    }

    public int getInt(String name, int fallback) {
        try {
            return Integer.parseInt(params.get(name));
        } catch (NumberFormatException | NullPointerException e) {
            return fallback;
        }
    }

    public boolean has(String name) {
        return params.containsKey(name);
    }

    public Map<String, String> asMap() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RouteParams r && Objects.equals(params, r.params);
    }

    @Override
    public int hashCode() {
        return params.hashCode();
    }

    @Override
    public String toString() {
        return "RouteParams" + params;
    }
}
