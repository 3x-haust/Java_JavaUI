package io.github._3xhaust.dsl;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.enums.MainAxisAlignment;
import io.github._3xhaust.dsl.enums.CrossAxisAlignment;
import io.github._3xhaust.dsl.enums.MainAxisSize;
import io.github._3xhaust.platform.swing.SwingRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Layouts {
    public static ColumnWidget Column(View... children) {
        return new ColumnWidget(Arrays.asList(children));
    }

    public static CenterWidget Center(View child) {
        return new CenterWidget(child);
    }

    public static RowWidget Row(View... children) {
        return new RowWidget(Arrays.asList(children));
    }

    public static SizedBoxWidget SizedBox(double width, double height) {
        return new SizedBoxWidget(width, height);
    }

    public static SizedBoxWidget SizedBox(double width, double height, View child) {
        return new SizedBoxWidget(width, height, child);
    }

    public static ExpandedWidget Expanded(View child) {
        return new ExpandedWidget(child, 1);
    }

    public static ExpandedWidget Expanded(View child, int flex) {
        return new ExpandedWidget(child, flex);
    }

    public static FlexibleWidget Flexible(View child) {
        return new FlexibleWidget(child, 1);
    }

    public static FlexibleWidget Flexible(View child, int flex) {
        return new FlexibleWidget(child, flex);
    }

    public static class ColumnWidget implements View {
        private final List<View> children;
        private MainAxisAlignment mainAxisAlignment = MainAxisAlignment.START;
        private CrossAxisAlignment crossAxisAlignment = CrossAxisAlignment.CENTER;
        private MainAxisSize mainAxisSize = MainAxisSize.MAX;
        private Insets padding;
        private int gap = 0;

        public ColumnWidget(List<View> children) {
            this.children = children;
        }

        public ColumnWidget mainAxisAlignment(MainAxisAlignment alignment) {
            this.mainAxisAlignment = alignment;
            return this;
        }

        public ColumnWidget crossAxisAlignment(CrossAxisAlignment alignment) {
            this.crossAxisAlignment = alignment;
            return this;
        }

        public ColumnWidget mainAxisSize(MainAxisSize size) {
            this.mainAxisSize = size;
            return this;
        }

        public ColumnWidget padding(int all) {
            this.padding = new Insets(all, all, all, all);
            return this;
        }

        public ColumnWidget padding(int vertical, int horizontal) {
            this.padding = new Insets(vertical, horizontal, vertical, horizontal);
            return this;
        }

        public ColumnWidget gap(int gap) {
            this.gap = gap;
            return this;
        }

        @Override
        public void render(Renderer renderer) {
            if (renderer instanceof SwingRenderer swing) {
                JPanel panel = createColumnPanel();

                if (padding != null) {
                    panel.setBorder(BorderFactory.createEmptyBorder(
                        padding.top, padding.left, padding.bottom, padding.right));
                }

                swing.pushContainer(panel);

                addMainAxisSpacing(panel, true);

                for (int i = 0; i < children.size(); i++) {
                    if (crossAxisAlignment != CrossAxisAlignment.STRETCH) {
                        JPanel wrapper = createCrossAxisWrapper();
                        swing.pushContainer(wrapper);
                        children.get(i).render(renderer);
                        swing.popContainer();

                        // crossAxisAlignment에 따라 추가 glue 처리
                        switch (crossAxisAlignment) {
                            case START -> {
                                // START: 왼쪽 정렬이므로 오른쪽에 glue 추가
                                wrapper.add(Box.createHorizontalGlue());
                            }
                            case CENTER -> {
                                wrapper.add(Box.createHorizontalGlue());
                            }
                            case END -> {
                                // END: 오른쪽 정렬이므로 이미 createCrossAxisWrapper에서 처리됨
                            }
                        }

                        panel.add(wrapper);
                    } else {
                        children.get(i).render(renderer);
                    }

                    if (i < children.size() - 1) {
                        if (gap > 0) {
                            panel.add(Box.createVerticalStrut(gap));
                        }
                        addMainAxisSpacing(panel, false);
                    }
                }

                addMainAxisSpacing(panel, true);

                swing.popContainer();
                swing.addComponent(panel);
            }
        }

        private JPanel createColumnPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            return panel;
        }

        private void addMainAxisSpacing(JPanel panel, boolean isEdge) {
            switch (mainAxisAlignment) {
                case CENTER -> {
                    if (isEdge) panel.add(Box.createVerticalGlue());
                }
                case END -> {
                    if (isEdge && panel.getComponentCount() == 0) {
                        panel.add(Box.createVerticalGlue());
                    }
                }
                case SPACE_BETWEEN -> {
                    if (!isEdge && panel.getComponentCount() > 0) {
                        panel.add(Box.createVerticalGlue());
                    }
                }
                case SPACE_AROUND -> panel.add(Box.createVerticalGlue());
                case SPACE_EVENLY -> panel.add(Box.createVerticalGlue());
            }
        }

        private JPanel createCrossAxisWrapper() {
            JPanel wrapper = new JPanel();
            wrapper.setOpaque(false);
            wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));

            switch (crossAxisAlignment) {
                case START -> {
                    // START의 경우 컴포넌��를 왼쪽에 배치하고 오른쪽에 여백을 추가
                }
                case END -> {
                    wrapper.add(Box.createHorizontalGlue());
                }
                case CENTER -> {
                    wrapper.add(Box.createHorizontalGlue());
                }
                default -> {
                    wrapper.add(Box.createHorizontalGlue());
                }
            }

            return wrapper;
        }
    }

    public static class RowWidget implements View {
        private final List<View> children;
        private MainAxisAlignment mainAxisAlignment = MainAxisAlignment.START;
        private CrossAxisAlignment crossAxisAlignment = CrossAxisAlignment.CENTER;
        private MainAxisSize mainAxisSize = MainAxisSize.MAX;
        private Insets padding;
        private int gap = 0;

        public RowWidget(List<View> children) {
            this.children = children;
        }

        public RowWidget mainAxisAlignment(MainAxisAlignment alignment) {
            this.mainAxisAlignment = alignment;
            return this;
        }

        public RowWidget crossAxisAlignment(CrossAxisAlignment alignment) {
            this.crossAxisAlignment = alignment;
            return this;
        }

        public RowWidget mainAxisSize(MainAxisSize size) {
            this.mainAxisSize = size;
            return this;
        }

        public RowWidget padding(int all) {
            this.padding = new Insets(all, all, all, all);
            return this;
        }

        public RowWidget padding(int vertical, int horizontal) {
            this.padding = new Insets(vertical, horizontal, vertical, horizontal);
            return this;
        }

        public RowWidget gap(int gap) {
            this.gap = gap;
            return this;
        }

        @Override
        public void render(Renderer renderer) {
            if (renderer instanceof SwingRenderer swing) {
                JPanel panel = createRowPanel();

                if (padding != null) {
                    panel.setBorder(BorderFactory.createEmptyBorder(
                        padding.top, padding.left, padding.bottom, padding.right));
                }

                swing.pushContainer(panel);

                addMainAxisSpacing(panel, true);

                for (int i = 0; i < children.size(); i++) {
                    children.get(i).render(renderer);

                    if (i < children.size() - 1) {
                        if (gap > 0) {
                            panel.add(Box.createHorizontalStrut(gap));
                        }
                        addMainAxisSpacing(panel, false);
                    }
                }

                addMainAxisSpacing(panel, true);

                swing.popContainer();
                swing.addComponent(panel);
            }
        }

        private JPanel createRowPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            switch (crossAxisAlignment) {
                case START -> panel.setAlignmentY(Component.TOP_ALIGNMENT);
                case END -> panel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                case CENTER -> panel.setAlignmentY(Component.CENTER_ALIGNMENT);
                case STRETCH -> panel.setAlignmentY(Component.CENTER_ALIGNMENT);
            }

            return panel;
        }

        private void addMainAxisSpacing(JPanel panel, boolean isEdge) {
            switch (mainAxisAlignment) {
                case CENTER -> {
                    if (isEdge) panel.add(Box.createHorizontalGlue());
                }
                case END -> {
                    if (isEdge && panel.getComponentCount() == 0) {
                        panel.add(Box.createHorizontalGlue());
                    }
                }
                case SPACE_BETWEEN -> {
                    if (!isEdge && panel.getComponentCount() > 0) {
                        panel.add(Box.createHorizontalGlue());
                    }
                }
                case SPACE_AROUND -> panel.add(Box.createHorizontalGlue());
                case SPACE_EVENLY -> panel.add(Box.createHorizontalGlue());
            }
        }
    }

    public static class CenterWidget implements View {
        private final View child;

        public CenterWidget(View child) {
            this.child = child;
        }

        @Override
        public void render(Renderer renderer) {
            if (renderer instanceof SwingRenderer swing) {
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.anchor = GridBagConstraints.CENTER;

                swing.pushContainer(panel);
                child.render(renderer);
                swing.popContainer();

                swing.addComponent(panel);
            }
        }
    }

    public static class SizedBoxWidget implements View {
        private final double width;
        private final double height;
        private final View child;

        public SizedBoxWidget(double width, double height) {
            this.width = width;
            this.height = height;
            this.child = null;
        }

        public SizedBoxWidget(double width, double height, View child) {
            this.width = width;
            this.height = height;
            this.child = child;
        }

        @Override
        public void render(Renderer renderer) {
            if (renderer instanceof SwingRenderer swing) {
                JPanel panel = new JPanel();
                panel.setPreferredSize(new Dimension((int)width, (int)height));
                panel.setMinimumSize(new Dimension((int)width, (int)height));
                panel.setMaximumSize(new Dimension((int)width, (int)height));

                if (child != null) {
                    swing.pushContainer(panel);
                    child.render(renderer);
                    swing.popContainer();
                }

                swing.addComponent(panel);
            }
        }
    }

    public static class ExpandedWidget implements View {
        private final View child;
        private final int flex;

        public ExpandedWidget(View child, int flex) {
            this.child = child;
            this.flex = flex;
        }

        @Override
        public void render(Renderer renderer) {
            child.render(renderer);
        }
    }

    public static class FlexibleWidget implements View {
        private final View child;
        private final int flex;

        public FlexibleWidget(View child, int flex) {
            this.child = child;
            this.flex = flex;
        }

        @Override
        public void render(Renderer renderer) {
            child.render(renderer);
        }
    }
}
