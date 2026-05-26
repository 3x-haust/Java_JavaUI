package io.github._3xhaust.dsl.widgets;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.state.State;

/**
 * Calendar date picker bound to a {@link State} of {@link LocalDate}. Backed by
 * a {@link JSpinner} with date model so it works on every Swing platform.
 */
public final class DatePickerWidget extends AbstractWidget<DatePickerWidget> {
    private final State<LocalDate> state;
    private Consumer<LocalDate> onChange;

    public DatePickerWidget(State<LocalDate> state) {
        this.state = state;
    }

    public static DatePickerWidget DatePicker(State<LocalDate> state) {
        return new DatePickerWidget(state);
    }

    public DatePickerWidget onChange(Consumer<LocalDate> c) { this.onChange = c; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        SpinnerDateModel model = new SpinnerDateModel();
        if (state.get() != null) {
            model.setValue(toDate(state.get()));
        }
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        spinner.addChangeListener(e -> {
            LocalDate now = toLocalDate((Date) spinner.getValue());
            state.set(now);
            if (onChange != null) onChange.accept(now);
        });
        SwingRenderer.applyStyles(spinner, getStyle());
        swing.addComponent(spinner);
    }

    private static Date toDate(LocalDate d) {
        return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDate toLocalDate(Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
