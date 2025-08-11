package io.github._3xhaust.platform.swing;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.theme.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Stack;

public class SwingRenderer implements Renderer {
    private JFrame frame;
    private final Stack<Container> containerStack = new Stack<>();

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        applyMaterialDesignDefaults();
    }

    private static void applyMaterialDesignDefaults() {
        Font defaultFont = new Font("SF Pro Display", Font.PLAIN, 14);
        Font buttonFont = new Font("SF Pro Display", Font.PLAIN, 14);

        UIManager.put("Button.font", buttonFont);
        UIManager.put("Button.background", Colors.Blue500);
        UIManager.put("Button.foreground", Colors.White);
        UIManager.put("Button.focusPainted", false);
        UIManager.put("Button.borderPainted", false);

        UIManager.put("Label.font", defaultFont);
        UIManager.put("Label.foreground", Colors.Grey900);

        UIManager.put("Panel.background", Colors.Grey50);
    }

    @Override
    public void init(String title, int width, int height) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(width, height);
            frame.setLocationRelativeTo(null);

            JPanel rootPanel = new JPanel();
            rootPanel.setLayout(new BorderLayout());
            rootPanel.setBackground(Colors.Grey50);
            frame.setContentPane(rootPanel);
            frame.setVisible(true);

            containerStack.clear();
            containerStack.push(rootPanel);
        });
    }

    @Override
    public void mount(View root) {
        SwingUtilities.invokeLater(() -> {
            if (containerStack.isEmpty()) {
                System.err.println("containerStack is empty!");
                return;
            }

            Container rootContainer = containerStack.peek();
            rootContainer.removeAll();
            root.render(this);
            rootContainer.revalidate();
            rootContainer.repaint();
        });
    }

    @Override
    public void update(View oldView, View newView) {
    }

    @Override
    public void unmount(View view) {
    }

    @Override
    public void renderContainer(String type, List<View> children) {
        JPanel panel = createStyledPanel();

        switch (type) {
            case "column" -> panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            case "row" -> panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            case "center" -> panel.setLayout(new GridBagLayout());
            default -> panel.setLayout(new FlowLayout());
        }

        pushContainer(panel);

        if ("center".equals(type)) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;

            JPanel childPanel = createStyledPanel();
            if (children.size() == 1) {
                pushContainer(childPanel);
                children.get(0).render(this);
                popContainer();
            } else {
                childPanel.setLayout(new BoxLayout(childPanel, BoxLayout.Y_AXIS));
                pushContainer(childPanel);
                for (View child : children) {
                    child.render(this);
                }
                popContainer();
            }

            panel.add(childPanel, gbc);
        } else if ("row".equals(type)) {
            panel.add(Box.createHorizontalGlue());
            for (int i = 0; i < children.size(); i++) {
                children.get(i).render(this);

                if (i < children.size() - 1) {
                    panel.add(Box.createHorizontalStrut(8));
                }
            }
            panel.add(Box.createHorizontalGlue());
        } else if ("column".equals(type)) {
            for (int i = 0; i < children.size(); i++) {
                children.get(i).render(this);

                if (i < children.size() - 1) {
                    panel.add(Box.createVerticalStrut(8));
                }
            }
        }

        popContainer();

        Container parent = containerStack.peek();

        if (parent.getLayout() instanceof BorderLayout && "center".equals(type)) {
            parent.add(panel, BorderLayout.CENTER);
        } else {
            parent.add(panel);
        }
    }

    @Override
    public void pushContainer(Container container) {
        containerStack.push(container);
    }

    @Override
    public void popContainer() {
        containerStack.pop();
    }

    @Override
    public void addComponent(Component comp) {
        if (!containerStack.isEmpty()) {
            containerStack.peek().add(comp);
        } else {
            System.err.println("Error: No container to add component");
        }
    }

    public JButton button(String text, Runnable onClick) {
        JButton btn = new JButton(text);

        styleMaterialButton(btn);

        btn.addActionListener(e -> onClick.run());
        return btn;
    }

    public JLabel label(String text) {
        JLabel label = new JLabel(text);
        styleMaterialLabel(label);
        return label;
    }

    public <T> JLabel label(io.github._3xhaust.state.State<T> state) {
        JLabel lbl = new JLabel(state.get().toString());
        styleMaterialLabel(lbl);
        state.bindOnChange(newVal -> SwingUtilities.invokeLater(() -> lbl.setText(newVal.toString())));
        return lbl;
    }

    private JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Colors.Grey50);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        return panel;
    }

    private void styleMaterialButton(JButton button) {
        button.setBackground(Colors.Blue500);
        button.setForeground(Colors.White);
        button.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        button.setAlignmentY(Component.CENTER_ALIGNMENT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(button.getPreferredSize());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(Colors.Blue600);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(Colors.Blue500);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(Colors.Blue700);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(Colors.Blue600);
            }
        });
    }

    private void styleMaterialLabel(JLabel label) {
        label.setForeground(Colors.Grey900);
        label.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setAlignmentY(Component.CENTER_ALIGNMENT);
    }

    public JButton primaryButton(String text, Runnable onClick) {
        JButton btn = button(text, onClick);
        btn.setBackground(Colors.Blue500);
        return btn;
    }

    public JButton secondaryButton(String text, Runnable onClick) {
        JButton btn = button(text, onClick);
        btn.setBackground(Colors.Grey500);
        btn.setForeground(Colors.White);
        return btn;
    }

    public JButton successButton(String text, Runnable onClick) {
        JButton btn = button(text, onClick);
        btn.setBackground(Colors.Green500);
        btn.setForeground(Colors.White);
        return btn;
    }

    public JButton dangerButton(String text, Runnable onClick) {
        JButton btn = button(text, onClick);
        btn.setBackground(Colors.Red500);
        btn.setForeground(Colors.White);
        return btn;
    }

    public JButton warningButton(String text, Runnable onClick) {
        JButton btn = button(text, onClick);
        btn.setBackground(Colors.Orange500);
        btn.setForeground(Colors.White);
        return btn;
    }
}
