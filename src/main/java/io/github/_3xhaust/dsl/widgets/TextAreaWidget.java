package io.github._3xhaust.dsl.widgets;

import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.state.State;

/**
 * Multi-line text input bound to a {@link State}.
 */
public final class TextAreaWidget extends AbstractWidget<TextAreaWidget> {
    private final State<String> state;
    private int rows = 4;
    private int columns = 30;
    private boolean lineWrap = true;
    private String placeholder;

    public TextAreaWidget(State<String> state) {
        this.state = state;
    }

    public static TextAreaWidget TextArea(State<String> state) {
        return new TextAreaWidget(state);
    }

    public TextAreaWidget rows(int r) { this.rows = r; return this; }
    public TextAreaWidget columns(int c) { this.columns = c; return this; }
    public TextAreaWidget lineWrap(boolean v) { this.lineWrap = v; return this; }
    public TextAreaWidget placeholder(String s) { this.placeholder = s; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JTextArea area = new JTextArea(rows, columns);
        area.setLineWrap(lineWrap);
        area.setWrapStyleWord(lineWrap);
        if (state.get() != null) area.setText(state.get());
        if (placeholder != null) area.setToolTipText(placeholder);
        area.getDocument().addDocumentListener(new DocumentListener() {
            void update() { state.set(area.getText()); }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });
        SwingRenderer.applyStyles(area, getStyle());
        swing.addComponent(new JScrollPane(area));
    }
}
