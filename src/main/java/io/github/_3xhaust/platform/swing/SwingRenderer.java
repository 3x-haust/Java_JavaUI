package io.github._3xhaust.platform.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import io.github._3xhaust.core.JavaUIApp;
import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.Style;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.theme.ThemeData;
import io.github._3xhaust.util.Fonts;
import io.github._3xhaust.util.Logger;

/**
 * Swing-backed implementation of {@link Renderer}. The renderer keeps an explicit
 * stack of containers as widgets are visited; each widget pushes a Swing container,
 * renders its children into it, then pops.
 *
 * <p>Supports coalesced rebuilds via {@link #requestRebuild()} - any number of
 * {@code setState} calls during a frame produce a single re-render on the EDT.</p>
 */
public class SwingRenderer implements Renderer {
    private static final Logger LOG = Logger.get(SwingRenderer.class);

    private JFrame frame;
    private final Deque<Container> containerStack = new java.util.ArrayDeque<>();
    private final AtomicBoolean rebuildScheduled = new AtomicBoolean(false);
    private View lastRoot;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOG.warn("Failed to install system look-and-feel: " + e.getMessage());
        }
        applyDefaults();
    }

    private static void applyDefaults() {
        Font defaultFont = Fonts.sans(Font.PLAIN, 14);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("PasswordField.font", defaultFont);
        UIManager.put("CheckBox.font", defaultFont);
        UIManager.put("RadioButton.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("List.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TabbedPane.font", defaultFont);
        UIManager.put("Button.focusPainted", false);
    }

    public static void applyStyles(JComponent component, Style style) {
        if (style == null) return;
        if (component instanceof JButton button) {
            button.setContentAreaFilled(true);
        }

        if (style.getFont() != null) {
            component.setFont(style.getFont());
        }
        if (style.getForegroundColor() != null) {
            component.setForeground(style.getForegroundColor());
        }
        if (style.getBackgroundColor() != null) {
            component.setOpaque(true);
            component.setBackground(style.getBackgroundColor());
        }
        if (style.getHorizontalAlignment() != -1) {
            if (component instanceof JLabel label) {
                label.setHorizontalAlignment(style.getHorizontalAlignment());
            } else if (component instanceof AbstractButton b) {
                b.setHorizontalAlignment(style.getHorizontalAlignment());
            }
        }
        if (style.getVerticalAlignment() != -1) {
            if (component instanceof JLabel label) {
                label.setVerticalAlignment(style.getVerticalAlignment());
            } else if (component instanceof AbstractButton b) {
                b.setVerticalAlignment(style.getVerticalAlignment());
            }
        }
        Insets padding = style.getPadding();
        Style.Border border = style.getBorder();
        if (padding != null && border != null) {
            component.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(border.getColor(), border.getThickness()),
                    new EmptyBorder(padding.top, padding.left, padding.bottom, padding.right)));
        } else if (padding != null) {
            component.setBorder(new EmptyBorder(padding.top, padding.left, padding.bottom, padding.right));
        } else if (border != null) {
            component.setBorder(BorderFactory.createLineBorder(border.getColor(), border.getThickness()));
        }
        if (style.getSize() != null) {
            component.setPreferredSize(style.getSize());
        }
        if (style.getMinimumSize() != null) {
            component.setMinimumSize(style.getMinimumSize());
        }
        if (style.getMaximumSize() != null) {
            component.setMaximumSize(style.getMaximumSize());
        }
        if (style.getPreferredSize() != null) {
            component.setPreferredSize(style.getPreferredSize());
        }
        if (style.isOpaque()) {
            component.setOpaque(true);
        }
    }

    @Override
    public void init(String title, int width, int height) {
        SwingUtilities.invokeLater(() -> {
            ThemeData theme = Theme.get();
            frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(width, height);
            frame.setLocationRelativeTo(null);

            JPanel rootPanel = new JPanel(new java.awt.BorderLayout());
            rootPanel.setBackground(theme.background());
            frame.setContentPane(rootPanel);

            // Forward escape key to "back" navigation if there is history.
            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                @Override
                public void eventDispatched(AWTEvent event) {
                    if (event instanceof java.awt.event.KeyEvent ke
                            && ke.getID() == java.awt.event.KeyEvent.KEY_PRESSED
                            && ke.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                        // no-op; reserved hook for future global shortcuts
                    }
                }
            }, AWTEvent.KEY_EVENT_MASK);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    LOG.debug("Window closing");
                }
            });

            frame.setVisible(true);
            containerStack.clear();
            containerStack.push(rootPanel);
        });
    }

    @Override
    public void mount(View root) {
        this.lastRoot = root;
        SwingUtilities.invokeLater(() -> doMount(root));
    }

    private void doMount(View root) {
        if (containerStack.isEmpty()) {
            LOG.warn("doMount called before init; skipping");
            return;
        }
        Container rootContainer = peekRootContainer();
        // reset stack to just the root
        containerStack.clear();
        containerStack.push(rootContainer);
        rootContainer.removeAll();
        ThemeData t = Theme.get();
        rootContainer.setBackground(t.background());
        try {
            root.render(this);
        } catch (RuntimeException e) {
            LOG.error("Render failure: " + e.getMessage(), e);
            rootContainer.add(new JLabel("Render error: " + e.getMessage()));
        }
        rootContainer.revalidate();
        rootContainer.repaint();
    }

    private Container peekRootContainer() {
        // The root container is whatever was pushed first in init().
        return frame == null ? null : frame.getContentPane();
    }

    @Override
    public void requestRebuild() {
        if (frame == null) return;
        if (!rebuildScheduled.compareAndSet(false, true)) return;
        SwingUtilities.invokeLater(() -> {
            rebuildScheduled.set(false);
            JavaUIApp app = JavaUIApp.getInstance();
            View toMount = app != null ? app.currentView() : lastRoot;
            if (toMount != null) doMount(toMount);
        });
    }

    @Override
    public void pushContainer(Container container) {
        containerStack.push(container);
    }

    @Override
    public void popContainer() {
        if (!containerStack.isEmpty()) containerStack.pop();
    }

    @Override
    public void addComponent(Component comp) {
        if (containerStack.isEmpty()) {
            LOG.warn("addComponent called with empty container stack");
            return;
        }
        containerStack.peek().add(comp);
    }

    public Container currentContainer() {
        return containerStack.peek();
    }

    public JFrame getFrame() {
        return frame;
    }
}
