package io.github._3xhaust.dsl.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.state.State;
import io.github._3xhaust.theme.ColorUtils;
import io.github._3xhaust.theme.Theme;

/**
 * iOS-style toggle switch backed by a {@link State}.
 */
public final class SwitchWidget extends AbstractWidget<SwitchWidget> {
    private final State<Boolean> state;
    private Consumer<Boolean> onChange;

    public SwitchWidget(State<Boolean> state) {
        this.state = state;
    }

    public static SwitchWidget Switch(State<Boolean> state) {
        return new SwitchWidget(state);
    }

    public SwitchWidget onChange(Consumer<Boolean> v) { this.onChange = v; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        TogglePane pane = new TogglePane(state, onChange);
        SwingRenderer.applyStyles(pane, getStyle());
        swing.addComponent(pane);
    }

    private static final class TogglePane extends JComponent {
        private final State<Boolean> state;

        TogglePane(State<Boolean> state, Consumer<Boolean> onChange) {
            this.state = state;
            setPreferredSize(new Dimension(46, 26));
            setMaximumSize(new Dimension(46, 26));
            setMinimumSize(new Dimension(46, 26));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    boolean now = !(state.get() != null && state.get());
                    state.set(now);
                    if (onChange != null) onChange.accept(now);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                boolean on = state.get() != null && state.get();
                Color track = on ? Theme.get().primary() : ColorUtils.withAlpha(Theme.get().outline(), 200);
                g2.setColor(track);
                g2.fillRoundRect(0, 0, w, h, h, h);
                int knob = h - 4;
                int knobX = on ? w - knob - 2 : 2;
                g2.setColor(Color.WHITE);
                g2.fillOval(knobX, 2, knob, knob);
                g2.setColor(ColorUtils.withAlpha(Color.BLACK, 30));
                g2.setStroke(new BasicStroke(1));
                g2.drawOval(knobX, 2, knob, knob);
            } finally {
                g2.dispose();
            }
        }
    }
}
