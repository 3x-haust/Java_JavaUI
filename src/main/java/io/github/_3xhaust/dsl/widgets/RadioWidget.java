package io.github._3xhaust.dsl.widgets;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.JRadioButton;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.state.State;

/**
 * Single radio control bound to a {@link State}. The radio is "selected" when the
 * state value equals this widget's {@code value}.
 */
public final class RadioWidget<T> extends AbstractWidget<RadioWidget<T>> {
    private final State<T> state;
    private final T value;
    private final String label;
    private Consumer<T> onChange;

    public RadioWidget(State<T> state, T value, String label) {
        this.state = state;
        this.value = value;
        this.label = label;
    }

    public static <T> RadioWidget<T> Radio(State<T> state, T value, String label) {
        return new RadioWidget<>(state, value, label);
    }

    public RadioWidget<T> onChange(Consumer<T> c) { this.onChange = c; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JRadioButton btn = new JRadioButton(label != null ? label : "");
        btn.setOpaque(false);
        btn.setSelected(java.util.Objects.equals(state.get(), value));
        btn.addActionListener(e -> {
            state.set(value);
            if (onChange != null) onChange.accept(value);
        });
        SwingRenderer.applyStyles(btn, getStyle());
        swing.addComponent(btn);
    }
}
