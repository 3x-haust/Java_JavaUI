package io.github._3xhaust.dsl.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import io.github._3xhaust.core.JavaUIApp;
import io.github._3xhaust.core.View;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.util.Fonts;

/**
 * Modal dialog helper. Provides {@link #showAlert(String, String)},
 * {@link #showConfirm(String, String)} and {@link #showCustom(String, View)}.
 */
public final class DialogWidget {
    private DialogWidget() {}

    public static void showAlert(String title, String message) {
        ensureFrame();
        JOptionPane.showMessageDialog(frame(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(String title, String message) {
        ensureFrame();
        int result = JOptionPane.showConfirmDialog(frame(), message, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    public static String showPrompt(String title, String message, String initial) {
        ensureFrame();
        return JOptionPane.showInputDialog(frame(), message, initial);
    }

    /**
     * Open a custom dialog whose body is rendered via the framework. The dialog
     * is modal to the application window.
     */
    public static JDialog showCustom(String title, View body) {
        ensureFrame();
        JDialog dialog = new JDialog(frame(), title, true);
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Theme.get().surface());

        JLabel header = new JLabel(title);
        header.setFont(new Font(Fonts.defaultSans(), Font.BOLD, 16));
        header.setBorder(new javax.swing.border.EmptyBorder(16, 20, 8, 20));
        header.setForeground(Theme.get().onSurface());
        content.add(header, BorderLayout.NORTH);

        JPanel bodyContainer = new JPanel(new BorderLayout());
        bodyContainer.setOpaque(false);
        bodyContainer.setBorder(new javax.swing.border.EmptyBorder(8, 20, 12, 20));
        SwingRenderer renderer = JavaUIApp.getInstance().renderer();
        renderer.pushContainer(bodyContainer);
        if (body != null) body.render(renderer);
        renderer.popContainer();
        content.add(bodyContainer, BorderLayout.CENTER);

        JPanel actions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 8));
        actions.setOpaque(false);
        JButton close = new JButton("Close");
        close.addActionListener(e -> dialog.dispose());
        actions.add(close);
        content.add(actions, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setSize(new Dimension(420, 280));
        dialog.setLocationRelativeTo(frame());
        dialog.setVisible(true);
        return dialog;
    }

    private static javax.swing.JFrame frame() {
        return JavaUIApp.getInstance().getFrame();
    }

    private static void ensureFrame() {
        if (JavaUIApp.getInstance() == null || frame() == null) {
            throw new IllegalStateException("Cannot show dialog before JavaUIApp.run()");
        }
    }
}
