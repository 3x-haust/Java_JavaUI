package io.github._3xhaust.state;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Observable container for a single value. Listeners are notified whenever the
 * value is replaced via {@link #set(Object)} (or transformed via {@link #update(Function)}).
 *
 * <p>Multiple listeners are supported. Use {@link #subscribe(Consumer)} to add a
 * listener that you'll dispose later via the returned {@link AutoCloseable}.</p>
 */
public class State<T> {
    private T value;
    private final List<Consumer<T>> listeners = new CopyOnWriteArrayList<>();

    public State(T value) {
        this.value = value;
    }

    public static <T> State<T> of(T value) {
        return new State<>(value);
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        if (Objects.equals(this.value, value)) return;
        this.value = value;
        notifyListeners();
    }

    /** Set without equality check; useful for in-place mutations on collections. */
    public void setAlways(T value) {
        this.value = value;
        notifyListeners();
    }

    public void update(Function<T, T> updater) {
        set(updater.apply(value));
    }

    /** Mutate the held value in place then fire listeners. */
    public void mutate(Consumer<T> mutator) {
        mutator.accept(value);
        notifyListeners();
    }

    public AutoCloseable subscribe(Consumer<T> listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    /**
     * Legacy single-listener binding from the prototype API. Replaces any previously
     * bound legacy listener. New code should prefer {@link #subscribe(Consumer)}.
     */
    public void bindOnChange(Consumer<T> listener) {
        listeners.removeIf(l -> l instanceof LegacyListener);
        listeners.add(new LegacyListener<>(listener));
    }

    public int listenerCount() {
        return listeners.size();
    }

    private void notifyListeners() {
        for (Consumer<T> l : listeners) {
            try {
                l.accept(value);
            } catch (RuntimeException e) {
                // Don't let one bad listener break the others
                io.github._3xhaust.util.Logger.get(State.class)
                        .warn("State listener threw: " + e.getMessage(), e);
            }
        }
    }

    private static final class LegacyListener<T> implements Consumer<T> {
        private final Consumer<T> delegate;

        LegacyListener(Consumer<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void accept(T t) {
            delegate.accept(t);
        }
    }
}
