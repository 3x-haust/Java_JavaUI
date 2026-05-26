package io.github._3xhaust.dsl.widgets;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.state.State;

public final class DropdownWidget<T> extends AbstractWidget<DropdownWidget<T>> {
    private final State<T> state;
    private final List<T> items;
    private Consumer<T> onChange;

    public DropdownWidget(State<T> state, List<T> items) {
        this.state = state;
        this.items = items;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> DropdownWidget<T> Dropdown(State<T> state, T... items) {
        return new DropdownWidget<>(state, Arrays.asList(items));
    }

    public static <T> DropdownWidget<T> Dropdown(State<T> state, List<T> items) {
        return new DropdownWidget<>(state, items);
    }

    public DropdownWidget<T> onChange(Consumer<T> c) { this.onChange = c; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();
        for (T item : items) model.addElement(item);
        JComboBox<T> combo = new JComboBox<>(model);
        if (state.get() != null) combo.setSelectedItem(state.get());
        combo.addActionListener(e -> {
            @SuppressWarnings("unchecked")
            T sel = (T) combo.getSelectedItem();
            state.set(sel);
            if (onChange != null) onChange.accept(sel);
        });
        SwingRenderer.applyStyles(combo, getStyle());
        swing.addComponent(combo);
    }
}
