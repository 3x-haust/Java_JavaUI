package io.github._3xhaust.dsl.widgets;

import java.util.List;

import javax.swing.JProgressBar;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.state.State;

public final class ProgressBarWidget extends AbstractWidget<ProgressBarWidget> {
    private final State<Integer> state;
    private int min;
    private int max = 100;
    private boolean indeterminate;
    private boolean showText;

    public ProgressBarWidget(State<Integer> state) {
        this.state = state;
    }

    public static ProgressBarWidget ProgressBar(State<Integer> state) {
        return new ProgressBarWidget(state);
    }

    public static ProgressBarWidget Indeterminate() {
        ProgressBarWidget w = new ProgressBarWidget(State.of(0));
        w.indeterminate = true;
        return w;
    }

    public ProgressBarWidget range(int lo, int hi) { this.min = lo; this.max = hi; return this; }
    public ProgressBarWidget showText(boolean v) { this.showText = v; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JProgressBar bar = new JProgressBar(min, max);
        bar.setOpaque(false);
        bar.setIndeterminate(indeterminate);
        bar.setStringPainted(showText);
        if (state != null && state.get() != null) {
            bar.setValue(Math.max(min, Math.min(max, state.get())));
            state.subscribe(v -> {
                if (v != null) bar.setValue(Math.max(min, Math.min(max, v)));
            });
        }
        SwingRenderer.applyStyles(bar, getStyle());
        swing.addComponent(bar);
    }
}
