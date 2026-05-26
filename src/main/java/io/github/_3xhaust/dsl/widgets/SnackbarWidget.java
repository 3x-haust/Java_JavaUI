package io.github._3xhaust.dsl.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import io.github._3xhaust.core.JavaUIApp;
import io.github._3xhaust.theme.ColorUtils;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.util.Fonts;

/**
 * Transient notification anchored to the bottom of the application window.
 * <pre>
 *   Snackbar.show("Saved!");
 *   Snackbar.show("Could not save", Snackbar.Type.ERROR, 3500);
 * </pre>
 */
public final class SnackbarWidget {
    public enum Type { INFO, SUCCESS, WARN, ERROR }

    private static volatile JWindow current;

    private SnackbarWidget() {}

    public static void show(String message) {
        show(message, Type.INFO, 2500);
    }

    public static void show(String message, Type type) {
        show(message, type, 2500);
    }

    public static void show(String message, Type type, int durationMs) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = JavaUIApp.getInstance() == null ? null : JavaUIApp.getInstance().getFrame();
            if (frame == null) return;
            if (current != null) current.dispose();

            JWindow window = new JWindow(frame);
            JPanel panel = new JPanel(new BorderLayout());
            Color bg = backgroundFor(type);
            panel.setBackground(bg);
            panel.setBorder(new EmptyBorder(12, 18, 12, 18));

            JLabel label = new JLabel(message);
            label.setForeground(ColorUtils.contrasting(bg));
            label.setFont(new Font(Fonts.defaultSans(), Font.PLAIN, 14));
            panel.add(label, BorderLayout.CENTER);

            window.setContentPane(panel);
            window.pack();

            Point loc = frame.getLocationOnScreen();
            Dimension size = frame.getSize();
            int x = loc.x + (size.width - window.getWidth()) / 2;
            int y = loc.y + size.height - window.getHeight() - 24;
            window.setLocation(x, y);
            window.setOpacity(0.95f);
            window.setVisible(true);
            current = window;

            ActionListener dismiss = e -> {
                if (current == window) {
                    window.dispose();
                    current = null;
                }
            };
            Timer t = new Timer(Math.max(500, durationMs), dismiss);
            t.setRepeats(false);
            t.start();
        });
    }

    private static Color backgroundFor(Type type) {
        return switch (type) {
            case SUCCESS -> new Color(46, 125, 50);
            case WARN -> new Color(237, 108, 2);
            case ERROR -> Theme.get().error();
            default -> new Color(50, 50, 50);
        };
    }

    /** Convenience alias for {@link #show(String)}. */
    public static class Snackbar {
        private Snackbar() {}
        public static void show(String message) { SnackbarWidget.show(message); }
        public static void show(String message, Type type) { SnackbarWidget.show(message, type); }
    }
}
