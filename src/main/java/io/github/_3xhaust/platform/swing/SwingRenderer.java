package io.github._3xhaust.platform.swing;

import io.github._3xhaust.core.Insets;
import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.enums.CrossAxisAlignment;
import io.github._3xhaust.dsl.enums.MainAxisAlignment;
import io.github._3xhaust.state.State;
import io.github._3xhaust.theme.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class SwingRenderer implements Renderer {
    private JFrame frame;
    private final Deque<ContainerContext> containerStack = new ArrayDeque<>();

    private static class ContainerContext {
        final JPanel panel;
        final String type; // column, row, center, sizedBox
        final MainAxisAlignment mainAxisAlignment;
        final CrossAxisAlignment crossAxisAlignment;
        final int gap;
        boolean firstChild = true;

        ContainerContext(JPanel panel, String type,
                         MainAxisAlignment mainAxisAlignment,
                         CrossAxisAlignment crossAxisAlignment,
                         int gap) {
            this.panel = panel;
            this.type = type;
            this.mainAxisAlignment = mainAxisAlignment;
            this.crossAxisAlignment = crossAxisAlignment;
            this.gap = gap;
        }
    }

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

            JPanel rootPanel = new JPanel(new BorderLayout());
            rootPanel.setBackground(Colors.Grey50);
            frame.setContentPane(rootPanel);
            frame.setVisible(true);

            containerStack.clear();
            // Use CENTER of BorderLayout as root container target
            JPanel content = new JPanel();
            content.setOpaque(true);
            content.setBackground(Colors.Grey50);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            rootPanel.add(content, BorderLayout.CENTER);
            containerStack.push(new ContainerContext(content, "column",
                    MainAxisAlignment.START, CrossAxisAlignment.START, 0));
        });
    }

    @Override
    public void mount(View root) {
        SwingUtilities.invokeLater(() -> {
            if (containerStack.isEmpty()) return;
            ContainerContext rootCtx = containerStack.peek();
            rootCtx.panel.removeAll();
            rootCtx.firstChild = true;
            root.render(this);
            rootCtx.panel.revalidate();
            rootCtx.panel.repaint();
        });
    }

    @Override
    public void update(View oldView, View newView) {
        // TODO: diff/reconcile
    }

    @Override
    public void unmount(View view) {
    }

    private JPanel createPanelWithPadding(Insets padding) {
        JPanel panel = new JPanel();
        panel.setBackground(Colors.Grey50);
        if (padding != null) {
            panel.setBorder(new EmptyBorder(padding.top, padding.left, padding.bottom, padding.right));
        } else {
            panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        }
        return panel;
    }

    private void push(JPanel panel, String type,
                      MainAxisAlignment mainAxisAlignment,
                      CrossAxisAlignment crossAxisAlignment,
                      int gap) {
        // Attach to parent
        if (!containerStack.isEmpty()) {
            ContainerContext parent = containerStack.peek();
            addToParent(parent, panel);
        }
        // Configure layout
        switch (type) {
            case "column" -> panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            case "row" -> panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            case "center" -> panel.setLayout(new GridBagLayout());
            case "sizedBox" -> panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        }
        ContainerContext ctx = new ContainerContext(panel, type, mainAxisAlignment, crossAxisAlignment, gap);
        // Leading glue for some alignments
        if ("column".equals(type)) {
            if (mainAxisAlignment == MainAxisAlignment.CENTER ||
                mainAxisAlignment == MainAxisAlignment.END ||
                mainAxisAlignment == MainAxisAlignment.SPACE_AROUND ||
                mainAxisAlignment == MainAxisAlignment.SPACE_EVENLY) {
                panel.add(Box.createVerticalGlue());
            }
        } else if ("row".equals(type)) {
            if (mainAxisAlignment == MainAxisAlignment.CENTER ||
                mainAxisAlignment == MainAxisAlignment.END ||
                mainAxisAlignment == MainAxisAlignment.SPACE_AROUND ||
                mainAxisAlignment == MainAxisAlignment.SPACE_EVENLY) {
                panel.add(Box.createHorizontalGlue());
            }
        }
        containerStack.push(ctx);
    }

    private void addToParent(ContainerContext parent, JComponent child) {
        switch (parent.type) {
            case "center" -> {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.CENTER;
                parent.panel.add(child, gbc);
            }
            default -> parent.panel.add(child);
        }
    }

    @Override
    public void pushColumn(MainAxisAlignment mainAxisAlignment, CrossAxisAlignment crossAxisAlignment, Insets padding, int gap) {
        JPanel panel = createPanelWithPadding(padding);
        push(panel, "column", mainAxisAlignment, crossAxisAlignment, gap);
    }

    @Override
    public void pushRow(MainAxisAlignment mainAxisAlignment, CrossAxisAlignment crossAxisAlignment, Insets padding, int gap) {
        JPanel panel = createPanelWithPadding(padding);
        push(panel, "row", mainAxisAlignment, crossAxisAlignment, gap);
    }

    @Override
    public void pushCenter() {
        JPanel panel = createPanelWithPadding(null);
        push(panel, "center", MainAxisAlignment.CENTER, CrossAxisAlignment.CENTER, 0);
    }

    @Override
    public void pushSizedBox(int width, int height) {
        JPanel panel = createPanelWithPadding(null);
        Dimension d = new Dimension(width, height);
        panel.setPreferredSize(d);
        panel.setMinimumSize(d);
        panel.setMaximumSize(d);
        push(panel, "sizedBox", MainAxisAlignment.START, CrossAxisAlignment.START, 0);
    }

    @Override
    public void pop() {
        if (containerStack.isEmpty()) return;
        ContainerContext ctx = containerStack.pop();
        // Trailing glue for alignment
        if ("column".equals(ctx.type)) {
            if (ctx.mainAxisAlignment == MainAxisAlignment.CENTER ||
                ctx.mainAxisAlignment == MainAxisAlignment.SPACE_AROUND ||
                ctx.mainAxisAlignment == MainAxisAlignment.SPACE_EVENLY) {
                ctx.panel.add(Box.createVerticalGlue());
            }
        } else if ("row".equals(ctx.type)) {
            if (ctx.mainAxisAlignment == MainAxisAlignment.CENTER ||
                ctx.mainAxisAlignment == MainAxisAlignment.SPACE_AROUND ||
                ctx.mainAxisAlignment == MainAxisAlignment.SPACE_EVENLY) {
                ctx.panel.add(Box.createHorizontalGlue());
            }
        }
    }

    private void beforeAddChild(ContainerContext ctx) {
        if (!ctx.firstChild) {
            if ("column".equals(ctx.type)) {
                if (ctx.gap > 0) ctx.panel.add(Box.createVerticalStrut(ctx.gap));
                if (ctx.mainAxisAlignment == MainAxisAlignment.SPACE_BETWEEN ||
                    ctx.mainAxisAlignment == MainAxisAlignment.SPACE_AROUND ||
                    ctx.mainAxisAlignment == MainAxisAlignment.SPACE_EVENLY) {
                    ctx.panel.add(Box.createVerticalGlue());
                }
            } else if ("row".equals(ctx.type)) {
                if (ctx.gap > 0) ctx.panel.add(Box.createHorizontalStrut(ctx.gap));
                if (ctx.mainAxisAlignment == MainAxisAlignment.SPACE_BETWEEN ||
                    ctx.mainAxisAlignment == MainAxisAlignment.SPACE_AROUND ||
                    ctx.mainAxisAlignment == MainAxisAlignment.SPACE_EVENLY) {
                    ctx.panel.add(Box.createHorizontalGlue());
                }
            }
        }
        ctx.firstChild = false;
    }

    private void applyCrossAlignment(ContainerContext ctx, JComponent comp) {
        if ("column".equals(ctx.type)) {
            switch (ctx.crossAxisAlignment) {
                case START -> comp.setAlignmentX(Component.LEFT_ALIGNMENT);
                case CENTER -> comp.setAlignmentX(Component.CENTER_ALIGNMENT);
                case END -> comp.setAlignmentX(Component.RIGHT_ALIGNMENT);
                case STRETCH -> {
                    comp.setAlignmentX(Component.CENTER_ALIGNMENT);
                }
                default -> comp.setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        } else if ("row".equals(ctx.type)) {
            switch (ctx.crossAxisAlignment) {
                case START -> comp.setAlignmentY(Component.TOP_ALIGNMENT);
                case CENTER -> comp.setAlignmentY(Component.CENTER_ALIGNMENT);
                case END -> comp.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                case STRETCH -> comp.setAlignmentY(Component.CENTER_ALIGNMENT);
                default -> comp.setAlignmentY(Component.CENTER_ALIGNMENT);
            }
        }
    }

    @Override
    public void addText(String text) {
        JLabel label = new JLabel(text);
        styleMaterialLabel(label);
        if (!containerStack.isEmpty()) {
            ContainerContext ctx = containerStack.peek();
            beforeAddChild(ctx);
            applyCrossAlignment(ctx, label);
            addToParent(ctx, label);
        }
    }

    @Override
    public <T> void addText(State<T> state) {
        JLabel lbl = new JLabel(state.get() != null ? state.get().toString() : "");
        styleMaterialLabel(lbl);
        state.bindOnChange(newVal -> SwingUtilities.invokeLater(() -> lbl.setText(newVal != null ? newVal.toString() : "")));
        if (!containerStack.isEmpty()) {
            ContainerContext ctx = containerStack.peek();
            beforeAddChild(ctx);
            applyCrossAlignment(ctx, lbl);
            addToParent(ctx, lbl);
        }
    }

    @Override
    public void addButton(String text, Runnable onClick) {
        JButton btn = new JButton(text);
        styleMaterialButton(btn);
        btn.addActionListener(e -> onClick.run());
        if (!containerStack.isEmpty()) {
            ContainerContext ctx = containerStack.peek();
            beforeAddChild(ctx);
            applyCrossAlignment(ctx, btn);
            addToParent(ctx, btn);
        }
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
        button.setMaximumSize(button.getPreferredSize());
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(Colors.Blue600); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(Colors.Blue500); }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { button.setBackground(Colors.Blue700); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { button.setBackground(Colors.Blue600); }
        });
    }

    private void styleMaterialLabel(JLabel label) {
        label.setForeground(Colors.Grey900);
        label.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
    }
}
