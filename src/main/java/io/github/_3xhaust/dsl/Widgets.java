package io.github._3xhaust.dsl;

import io.github._3xhaust.core.View;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.state.State;

import javax.swing.*;
import java.awt.*;

public class Widgets {

    public static TextWidget Text(String value) {
        return new TextWidget(value);
    }

    public static TextWidget Text(State<?> state) {
        return new TextWidget(state);
    }

    public static ButtonWidget Button(String text, Runnable onClick) {
        return new ButtonWidget(text, onClick, "primary");
    }

    public static TextWidget Label(String text) {
        return new TextWidget(text, true);
    }

    public static TextWidget Label(State<?> state) {
        return new TextWidget(state, true);
    }

    public static class TextWidget implements View {
        private final String text;
        private final State<?> state;
        private final boolean isLabel;

        private Font font;
        private Color color;
        private Color backgroundColor;
        private int textAlign = -1;
        private Insets padding;
        private BorderInfo border;
        private Dimension minSize;

        public TextWidget(String text) {
            this.text = text;
            this.state = null;
            this.isLabel = false;
        }

        public TextWidget(State<?> state) {
            this.text = null;
            this.state = state;
            this.isLabel = false;
        }

        public TextWidget(String text, boolean isLabel) {
            this.text = text;
            this.state = null;
            this.isLabel = isLabel;
        }

        public TextWidget(State<?> state, boolean isLabel) {
            this.text = null;
            this.state = state;
            this.isLabel = isLabel;
        }

        public TextWidget font(Mod.FontModifier... modifiers) {
            if (modifiers.length == 1) {
                this.font = modifiers[0].toFont();
            } else {
                String family = Font.SANS_SERIF;
                int size = 12;
                int weight = Font.PLAIN;

                for (Mod.FontModifier mod : modifiers) {
                    if (mod.getFamily() != null) family = mod.getFamily();
                    if (mod.getSize() != -1) size = mod.getSize();
                    if (mod.getWeight() != -1) weight = mod.getWeight();
                }

                this.font = new Font(family, weight, size);
            }
            return this;
        }

        public TextWidget color(Color color) {
            this.color = color;
            return this;
        }

        public TextWidget background(Color color) {
            this.backgroundColor = color;
            return this;
        }

        public TextWidget alignCenter() {
            this.textAlign = SwingConstants.CENTER;
            return this;
        }

        public TextWidget alignLeft() {
            this.textAlign = SwingConstants.LEFT;
            return this;
        }

        public TextWidget alignRight() {
            this.textAlign = SwingConstants.RIGHT;
            return this;
        }

        public TextWidget padding(int all) {
            this.padding = new Insets(all, all, all, all);
            return this;
        }

        public TextWidget padding(int vertical, int horizontal) {
            this.padding = new Insets(vertical, horizontal, vertical, horizontal);
            return this;
        }

        public TextWidget border(int width, Color color) {
            this.border = new BorderInfo(width, color);
            return this;
        }

        public TextWidget minWidth(int width) {
            this.minSize = new Dimension(width, 0);
            return this;
        }

        @Override
        public void render(io.github._3xhaust.core.Renderer renderer) {
            if (renderer instanceof SwingRenderer swing) {
                JLabel label;
                if (state != null) {
                    label = swing.label(state);
                } else {
                    label = swing.label(text);
                }

                applyModifiers(label);
                swing.addComponent(label);
            }
        }

        private void applyModifiers(JLabel label) {
            if (font != null) {
                label.setFont(font);
            }
            if (color != null) {
                label.setForeground(color);
            }
            if (backgroundColor != null) {
                label.setBackground(backgroundColor);
                label.setOpaque(true);
            }
            if (textAlign != -1) {
                label.setHorizontalAlignment(textAlign);
            }
            if (minSize != null) {
                label.setMinimumSize(minSize);
                label.setPreferredSize(minSize);
            }
            if (border != null) {
                label.setBorder(BorderFactory.createLineBorder(border.color, border.width));
            }
            if (padding != null) {
                javax.swing.border.Border currentBorder = label.getBorder();
                javax.swing.border.Border paddingBorder = BorderFactory.createEmptyBorder(
                    padding.top, padding.left, padding.bottom, padding.right
                );
                if (currentBorder != null) {
                    label.setBorder(BorderFactory.createCompoundBorder(currentBorder, paddingBorder));
                } else {
                    label.setBorder(paddingBorder);
                }
            }
        }
    }

    public static class ButtonWidget implements View {
        private final String text;
        private final Runnable onClick;
        private final String buttonType;

        private Font font;
        private Color color;
        private Color backgroundColor;
        private Insets padding;
        private BorderInfo border;
        private Dimension preferredSize;

        public ButtonWidget(String text, Runnable onClick) {
            this(text, onClick, "primary");
        }

        public ButtonWidget(String text, Runnable onClick, String buttonType) {
            this.text = text;
            this.onClick = onClick;
            this.buttonType = buttonType;
        }

        public ButtonWidget font(Mod.FontModifier... modifiers) {
            if (modifiers.length == 1) {
                this.font = modifiers[0].toFont();
            } else {
                String family = Font.SANS_SERIF;
                int size = 12;
                int weight = Font.PLAIN;

                for (Mod.FontModifier mod : modifiers) {
                    if (mod.getFamily() != null) family = mod.getFamily();
                    if (mod.getSize() != -1) size = mod.getSize();
                    if (mod.getWeight() != -1) weight = mod.getWeight();
                }

                this.font = new Font(family, weight, size);
            }
            return this;
        }

        public ButtonWidget color(Color color) {
            this.color = color;
            return this;
        }

        public ButtonWidget background(Color color) {
            this.backgroundColor = color;
            return this;
        }

        public ButtonWidget padding(int all) {
            this.padding = new Insets(all, all, all, all);
            return this;
        }

        public ButtonWidget padding(int vertical, int horizontal) {
            this.padding = new Insets(vertical, horizontal, vertical, horizontal);
            return this;
        }

        public ButtonWidget border(int width, Color color) {
            this.border = new BorderInfo(width, color);
            return this;
        }

        public ButtonWidget size(int width, int height) {
            this.preferredSize = new Dimension(width, height);
            return this;
        }

        @Override
        public void render(io.github._3xhaust.core.Renderer renderer) {
            if (renderer instanceof SwingRenderer swing) {
                JButton button;

                switch (buttonType) {
                    case "secondary" -> button = swing.secondaryButton(text, onClick);
                    case "success" -> button = swing.successButton(text, onClick);
                    case "danger" -> button = swing.dangerButton(text, onClick);
                    case "warning" -> button = swing.warningButton(text, onClick);
                    default -> button = swing.primaryButton(text, onClick);
                }

                applyModifiers(button);
                swing.addComponent(button);
            }
        }

        private void applyModifiers(JButton button) {
            if (font != null) {
                button.setFont(font);
            }
            if (color != null) {
                button.setForeground(color);
            }
            if (backgroundColor != null) {
                button.setBackground(backgroundColor);
            }
            if (preferredSize != null) {
                button.setPreferredSize(preferredSize);
            }
            if (border != null) {
                button.setBorder(BorderFactory.createLineBorder(border.color, border.width));
            }
            if (padding != null) {
                javax.swing.border.Border currentBorder = button.getBorder();
                javax.swing.border.Border paddingBorder = BorderFactory.createEmptyBorder(
                    padding.top, padding.left, padding.bottom, padding.right
                );
                if (currentBorder != null) {
                    button.setBorder(BorderFactory.createCompoundBorder(currentBorder, paddingBorder));
                } else {
                    button.setBorder(paddingBorder);
                }
            }
        }
    }

    private static class BorderInfo {
        final int width;
        final Color color;

        BorderInfo(int width, Color color) {
            this.width = width;
            this.color = color;
        }
    }
}
