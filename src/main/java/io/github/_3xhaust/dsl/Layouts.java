package io.github._3xhaust.dsl;

import io.github._3xhaust.core.Insets;
import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.enums.CrossAxisAlignment;
import io.github._3xhaust.dsl.enums.MainAxisAlignment;
import io.github._3xhaust.dsl.enums.MainAxisSize;

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
            this.padding = Insets.all(all);
            return this;
        }

        public ColumnWidget padding(int vertical, int horizontal) {
            this.padding = Insets.verticalHorizontal(vertical, horizontal);
            return this;
        }

        public ColumnWidget gap(int gap) {
            this.gap = gap;
            return this;
        }

        @Override
        public void render(Renderer renderer) {
            renderer.pushColumn(mainAxisAlignment, crossAxisAlignment, padding, gap);
            for (View child : children) {
                child.render(renderer);
            }
            renderer.pop();
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
            this.padding = Insets.all(all);
            return this;
        }

        public RowWidget padding(int vertical, int horizontal) {
            this.padding = Insets.verticalHorizontal(vertical, horizontal);
            return this;
        }

        public RowWidget gap(int gap) {
            this.gap = gap;
            return this;
        }

        @Override
        public void render(Renderer renderer) {
            renderer.pushRow(mainAxisAlignment, crossAxisAlignment, padding, gap);
            for (View child : children) {
                child.render(renderer);
            }
            renderer.pop();
        }
    }

    public static class CenterWidget implements View {
        private final View child;

        public CenterWidget(View child) {
            this.child = child;
        }

        @Override
        public void render(Renderer renderer) {
            renderer.pushCenter();
            child.render(renderer);
            renderer.pop();
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
            renderer.pushSizedBox((int) width, (int) height);
            if (child != null) {
                child.render(renderer);
            }
            renderer.pop();
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
