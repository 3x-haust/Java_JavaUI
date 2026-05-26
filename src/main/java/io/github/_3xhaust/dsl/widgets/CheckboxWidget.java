package io.github._3xhaust.dsl.widgets;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.JCheckBox;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.state.State;

public final class CheckboxWidget extends AbstractWidget<CheckboxWidget> {
    private final State<Boolean> state;
    private final String label;
    private Consumer<Boolean> onChange;

    public CheckboxWidget(State<Boolean> state, String label) {
        this.state = state;
        this.label = label;
    }

    public static CheckboxWidget Checkbox(State<Boolean> state) {
        return new CheckboxWidget(state, null);
    }

    public static CheckboxWidget Checkbox(State<Boolean> state, String label) {
        return new CheckboxWidget(state, label);
    }

    public CheckboxWidget onChange(Consumer<Boolean> v) { this.onChange = v; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JCheckBox box = new JCheckBox(label != null ? label : "");
        box.setOpaque(false);
        boolean v = state.get() != null && state.get();
        box.setSelected(v);
        box.addActionListener(e -> {
            boolean now = box.isSelected();
            state.set(now);
            if (onChange != null) onChange.accept(now);
        });
        SwingRenderer.applyStyles(box, getStyle());
        swing.addComponent(box);
    }
}
