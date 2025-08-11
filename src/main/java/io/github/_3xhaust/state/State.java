package io.github._3xhaust.state;

import java.util.function.Consumer;
import java.util.function.Function;

public class State<T> {
    private T value;
    private Consumer<T> onChange;

    public State(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        if (onChange != null) onChange.accept(value);
    }

    public void update(Function<T, T> updater) {
        set(updater.apply(value));
    }

    public void bindOnChange(Consumer<T> listener) {
        this.onChange = listener;
    }

    public static <T> State<T> of(T value) {
        return new State<>(value);
    }
}
