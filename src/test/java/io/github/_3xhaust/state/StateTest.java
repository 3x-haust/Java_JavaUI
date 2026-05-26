package io.github._3xhaust.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class StateTest {
    @Test
    void initialValueIsRetained() {
        State<Integer> s = State.of(42);
        assertEquals(42, s.get());
    }

    @Test
    void setEqualValueDoesNotFireListeners() {
        State<String> s = State.of("a");
        AtomicInteger fired = new AtomicInteger();
        s.subscribe(v -> fired.incrementAndGet());
        s.set("a");
        assertEquals(0, fired.get());
        s.set("b");
        assertEquals(1, fired.get());
    }

    @Test
    void multipleListenersAllReceiveUpdates() {
        State<Integer> s = State.of(0);
        AtomicInteger a = new AtomicInteger();
        AtomicInteger b = new AtomicInteger();
        s.subscribe(v -> a.set(v));
        s.subscribe(v -> b.set(v));
        s.set(7);
        assertEquals(7, a.get());
        assertEquals(7, b.get());
    }

    @Test
    void unsubscribeStopsNotifications() throws Exception {
        State<Integer> s = State.of(0);
        AtomicInteger fired = new AtomicInteger();
        AutoCloseable token = s.subscribe(v -> fired.incrementAndGet());
        s.set(1);
        token.close();
        s.set(2);
        assertEquals(1, fired.get());
    }

    @Test
    void mutateFiresListenersWithCurrentValue() {
        State<List<String>> s = State.of(new ArrayList<>());
        AtomicInteger fired = new AtomicInteger();
        s.subscribe(v -> fired.incrementAndGet());
        s.mutate(list -> list.add("x"));
        assertEquals(1, fired.get());
        assertEquals(1, s.get().size());
    }

    @Test
    void updateAppliesTransformAndFires() {
        State<Integer> s = State.of(2);
        AtomicInteger fired = new AtomicInteger();
        s.subscribe(v -> fired.incrementAndGet());
        s.update(v -> v * 5);
        assertEquals(10, s.get());
        assertEquals(1, fired.get());
    }

    @Test
    void listenerExceptionDoesNotKillOthers() {
        State<Integer> s = State.of(0);
        AtomicInteger goodFires = new AtomicInteger();
        s.subscribe(v -> { throw new RuntimeException("boom"); });
        s.subscribe(v -> goodFires.incrementAndGet());
        s.set(99);
        assertEquals(1, goodFires.get());
    }

    @Test
    void legacyBindOnChangeReplacesPrior() {
        State<String> s = State.of("");
        AtomicInteger fired = new AtomicInteger();
        s.bindOnChange(v -> fired.incrementAndGet());
        s.bindOnChange(v -> fired.addAndGet(10));
        s.set("hi");
        assertEquals(10, fired.get());
        assertTrue(s.listenerCount() >= 1);
        assertFalse(s.listenerCount() > 2);
    }
}
