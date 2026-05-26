package io.github._3xhaust.dsl.widgets;

import java.awt.Component;
import java.awt.Font;
import java.util.List;
import java.util.function.Function;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.util.Fonts;

/**
 * A labeled form field with optional inline error text. Pairs nicely with
 * {@link InputWidget}, {@link TextAreaWidget} and friends.
 *
 * <p>Validation: pass a function that returns an error message (or {@code null}
 * for "valid") and the field will refresh the error display whenever
 * {@link #validate()} is called.</p>
 */
public final class FormFieldWidget extends AbstractWidget<FormFieldWidget> {
    private final String label;
    private final View field;
    private String error;
    private Function<Void, String> validator;

    public FormFieldWidget(String label, View field) {
        this.label = label;
        this.field = field;
    }

    public static FormFieldWidget FormField(String label, View field) {
        return new FormFieldWidget(label, field);
    }

    public FormFieldWidget error(String e) { this.error = e; return this; }
    public FormFieldWidget validator(Function<Void, String> v) { this.validator = v; return this; }

    public boolean validate() {
        if (validator == null) return true;
        this.error = validator.apply(null);
        return error == null;
    }

    @Override
    public List<View> getChildren() {
        return field != null ? List.of(field) : List.of();
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(new Font(Fonts.defaultSans(), Font.BOLD, 12));
        l.setForeground(Theme.get().onSurface());
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        column.add(l);
        column.add(javax.swing.Box.createVerticalStrut(4));

        if (field != null) {
            swing.pushContainer(column);
            field.render(renderer);
            swing.popContainer();
        }

        if (error != null) {
            JLabel err = new JLabel(error);
            err.setFont(new Font(Fonts.defaultSans(), Font.PLAIN, 11));
            err.setForeground(Theme.get().error());
            err.setAlignmentX(Component.LEFT_ALIGNMENT);
            err.setBorder(new javax.swing.border.EmptyBorder(2, 0, 0, 0));
            column.add(err);
        }

        SwingRenderer.applyStyles(column, getStyle());
        swing.addComponent(column);
    }
}
