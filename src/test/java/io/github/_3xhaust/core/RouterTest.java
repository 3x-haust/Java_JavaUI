package io.github._3xhaust.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RouterTest {
    @Test
    void exactMatchResolves() {
        Router r = new Router();
        r.register("/home", () -> renderer -> {});
        assertNotNull(r.resolve("/home"));
    }

    @Test
    void pathParamCaptured() {
        Router r = new Router();
        StringBuilder seen = new StringBuilder();
        r.register("/users/:id", params -> {
            seen.append(params.get("id"));
            return renderer -> {};
        });
        r.navigate("/users/42");
        assertEquals("42", seen.toString());
    }

    @Test
    void unknownRouteReturnsNullAndIgnoresBack() {
        Router r = new Router();
        assertNull(r.resolve("/nope"));
        assertFalse(r.canGoBack());
        assertNull(r.back());
    }

    @Test
    void wildcardFallback() {
        Router r = new Router();
        r.register("/known", () -> renderer -> {});
        r.register("/*", () -> renderer -> {});
        assertNotNull(r.resolve("/anything-else"));
    }

    @Test
    void historyStackBackForward() {
        Router r = new Router();
        r.register("/a", () -> renderer -> {});
        r.register("/b", () -> renderer -> {});
        r.register("/c", () -> renderer -> {});
        r.navigate("/a");
        r.navigate("/b");
        r.navigate("/c");
        assertTrue(r.canGoBack());
        assertNotNull(r.back());
        assertEquals("/b", r.currentPath());
        assertNotNull(r.forward());
        assertEquals("/c", r.currentPath());
    }

    @Test
    void guardCanBlockNavigation() {
        Router r = new Router();
        r.register("/secret", () -> renderer -> {});
        r.guard(path -> !path.equals("/secret"));
        assertNull(r.navigate("/secret"));
    }

    @Test
    void onChangeListenerFires() {
        Router r = new Router();
        StringBuilder log = new StringBuilder();
        r.register("/x", () -> renderer -> {});
        r.onChange((path, params) -> log.append(path));
        r.navigate("/x");
        assertEquals("/x", log.toString());
    }

    @Test
    void paramIntFallback() {
        RouteParams p = new RouteParams(java.util.Map.of("n", "abc"));
        assertEquals(7, p.getInt("n", 7));
        assertEquals(123, p.getInt("missing", 123));
    }
}
