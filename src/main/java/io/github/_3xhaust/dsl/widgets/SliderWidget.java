package io.github._3xhaust.dsl.widgets;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.JSlider;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.state.State;

public final class SliderWidget extends AbstractWidget<SliderWidget> {
    private final State<Integer> state;
    private int min;
    private int max = 100;
    private boolean showTicks;
    private Consumer<Integer> onChange;

    public SliderWidget(State<Integer> state) {
        this.state = state;
    }

    public static SliderWidget Slider(State<Integer> state) {
        return new SliderWidget(state);
    }

    public SliderWidget min(int v) { this.min = v; return this; }
    public SliderWidget max(int v) { this.max = v; return this; }
    public SliderWidget range(int lo, int hi) { this.min = lo; this.max = hi; return this; }
    public SliderWidget showTicks(boolean v) { this.showTicks = v; return this; }
    public SliderWidget onChange(Consumer<Integer> c) { this.onChange = c; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        int initial = state.get() != null ? state.get() : min;
        JSlider slider = new JSlider(min, max, Math.max(min, Math.min(max, initial)));
        slider.setOpaque(false);
        slider.setPaintTicks(showTicks);
        slider.setPaintLabels(showTicks);
        slider.addChangeListener(e -> {
            int v = slider.getValue();
            state.set(v);
            if (onChange != null) onChange.accept(v);
        });
        SwingRenderer.applyStyles(slider, getStyle());
        swing.addComponent(slider);
    }
}
