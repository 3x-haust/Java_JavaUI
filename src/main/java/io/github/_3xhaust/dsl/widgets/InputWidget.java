package io.github._3xhaust.dsl.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.state.State;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.util.Fonts;

/**
 * Single-line text input bound to a {@link State}. Supports password mode and a
 * placeholder rendered when the field is empty and unfocused.
 */
public final class InputWidget extends AbstractWidget<InputWidget> {
    private final State<String> state;
    private boolean password;
    private String placeholder;
    private Integer columns;
    private Consumer<String> onChange;
    private Runnable onSubmit;
    private boolean enabled = true;

    public InputWidget(State<String> state) {
        this.state = state;
    }

    public static InputWidget Input(State<String> state) {
        return new InputWidget(state);
    }

    public InputWidget password(boolean v) { this.password = v; return this; }
    public InputWidget placeholder(String s) { this.placeholder = s; return this; }
    public InputWidget columns(int c) { this.columns = c; return this; }
    public InputWidget onChange(Consumer<String> c) { this.onChange = c; return this; }
    public InputWidget onSubmit(Runnable r) { this.onSubmit = r; return this; }
    public InputWidget enabled(boolean v) { this.enabled = v; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;

        JComponent field;
        if (password) {
            JPasswordField pf = new JPasswordField();
            if (columns != null) pf.setColumns(columns);
            if (state.get() != null) pf.setText(state.get());
            installPlaceholder(pf, placeholder);
            pf.getDocument().addDocumentListener(new DocumentListener() {
                void update() {
                    String v = new String(pf.getPassword());
                    state.set(v);
                    if (onChange != null) onChange.accept(v);
                }
                @Override public void insertUpdate(DocumentEvent e) { update(); }
                @Override public void removeUpdate(DocumentEvent e) { update(); }
                @Override public void changedUpdate(DocumentEvent e) { update(); }
            });
            if (onSubmit != null) pf.addActionListener(e -> onSubmit.run());
            field = pf;
        } else {
            JTextField tf = new JTextField();
            if (columns != null) tf.setColumns(columns);
            if (state.get() != null) tf.setText(state.get());
            installPlaceholder(tf, placeholder);
            tf.getDocument().addDocumentListener(new DocumentListener() {
                void update() {
                    state.set(tf.getText());
                    if (onChange != null) onChange.accept(tf.getText());
                }
                @Override public void insertUpdate(DocumentEvent e) { update(); }
                @Override public void removeUpdate(DocumentEvent e) { update(); }
                @Override public void changedUpdate(DocumentEvent e) { update(); }
            });
            if (onSubmit != null) tf.addActionListener(e -> onSubmit.run());
            field = tf;
        }

        field.setEnabled(enabled);
        field.setFont(new Font(Fonts.defaultSans(), Font.PLAIN, 14));
        field.setBackground(Theme.get().surface());
        field.setForeground(Theme.get().onSurface());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.get().outline(), 1, true),
                new EmptyBorder(6, 10, 6, 10)));

        SwingRenderer.applyStyles(field, getStyle());
        swing.addComponent(field);
    }

    private static void installPlaceholder(JTextField field, String placeholder) {
        if (placeholder == null || placeholder.isEmpty()) return;
        Color hint = new Color(120, 120, 120, 200);
        Color real = field.getForeground();
        if (field.getText().isEmpty()) {
            field.setForeground(hint);
            field.setText(placeholder);
        }
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)
                        && field.getForeground().equals(hint)) {
                    field.setText("");
                    field.setForeground(real);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(hint);
                    field.setText(placeholder);
                }
            }
        });
        // PasswordField placeholder is shown via tooltip too, to avoid revealing the
        // hint string as masked characters when the field receives focus.
        if (field instanceof JPasswordField) {
            field.setText("");
            field.setForeground(real);
            field.setToolTipText(placeholder);
        }
    }
}
