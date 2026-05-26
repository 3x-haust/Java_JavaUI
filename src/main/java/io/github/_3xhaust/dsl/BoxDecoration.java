package io.github._3xhaust.dsl;

import java.awt.*;

public class BoxDecoration {
    private Color color;
    private Border border;
    private BorderRadius borderRadius;
    private BoxShadow[] boxShadow;
    private Gradient gradient;
    private DecorationImage image;
    private BoxShape shape = BoxShape.RECTANGLE;

    public BoxDecoration() {}

    public BoxDecoration color(Color color) {
        this.color = color;
        return this;
    }

    public BoxDecoration border(Border border) {
        this.border = border;
        return this;
    }

    public BoxDecoration borderRadius(BorderRadius borderRadius) {
        this.borderRadius = borderRadius;
        return this;
    }

    public BoxDecoration boxShadow(BoxShadow... boxShadow) {
        this.boxShadow = boxShadow;
        return this;
    }

    public BoxDecoration gradient(Gradient gradient) {
        this.gradient = gradient;
        return this;
    }

    public BoxDecoration image(DecorationImage image) {
        this.image = image;
        return this;
    }

    public BoxDecoration shape(BoxShape shape) {
        this.shape = shape;
        return this;
    }

    public Color getColor() { return color; }
    public Border getBorder() { return border; }
    public BorderRadius getBorderRadius() { return borderRadius; }
    public BoxShadow[] getBoxShadow() { return boxShadow; }
    public Gradient getGradient() { return gradient; }
    public DecorationImage getImage() { return image; }
    public BoxShape getShape() { return shape; }

    public enum BoxShape {
        RECTANGLE, CIRCLE
    }

    public static class Border {
        private final Color color;
        private final double width;
        private final BorderStyle style;

        public Border(Color color, double width, BorderStyle style) {
            this.color = color;
            this.width = width;
            this.style = style;
        }

        public static Border all(Color color, double width) {
            return new Border(color, width, BorderStyle.SOLID);
        }

        public static Border all(Color color, double width, BorderStyle style) {
            return new Border(color, width, style);
        }

        public Color getColor() { return color; }
        public double getWidth() { return width; }
        public BorderStyle getStyle() { return style; }

        public enum BorderStyle {
            SOLID, DASHED, DOTTED
        }
    }

    public static class BorderRadius {
        private final double topLeft;
        private final double topRight;
        private final double bottomLeft;
        private final double bottomRight;

        public BorderRadius(double topLeft, double topRight, double bottomLeft, double bottomRight) {
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomLeft = bottomLeft;
            this.bottomRight = bottomRight;
        }

        public static BorderRadius all(double radius) {
            return new BorderRadius(radius, radius, radius, radius);
        }

        public static BorderRadius circular(double radius) {
            return all(radius);
        }

        public static BorderRadius only(double topLeft, double topRight, double bottomLeft, double bottomRight) {
            return new BorderRadius(topLeft, topRight, bottomLeft, bottomRight);
        }

        public double getTopLeft() { return topLeft; }
        public double getTopRight() { return topRight; }
        public double getBottomLeft() { return bottomLeft; }
        public double getBottomRight() { return bottomRight; }
    }

    public static class BoxShadow {
        private final Color color;
        private final double offsetX;
        private final double offsetY;
        private final double blurRadius;
        private final double spreadRadius;

        public BoxShadow(Color color, double offsetX, double offsetY, double blurRadius, double spreadRadius) {
            this.color = color;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.blurRadius = blurRadius;
            this.spreadRadius = spreadRadius;
        }

        public static BoxShadow create(Color color, double offsetX, double offsetY, double blurRadius) {
            return new BoxShadow(color, offsetX, offsetY, blurRadius, 0);
        }

        public Color getColor() { return color; }
        public double getOffsetX() { return offsetX; }
        public double getOffsetY() { return offsetY; }
        public double getBlurRadius() { return blurRadius; }
        public double getSpreadRadius() { return spreadRadius; }
    }

    public static abstract class Gradient {
        protected final Color[] colors;
        protected final double[] stops;

        public Gradient(Color[] colors, double[] stops) {
            this.colors = colors;
            this.stops = stops;
        }

        public Color[] getColors() { return colors; }
        public double[] getStops() { return stops; }
    }

    public static class LinearGradient extends Gradient {
        private final double startX, startY, endX, endY;

        public LinearGradient(Color[] colors, double[] stops, double startX, double startY, double endX, double endY) {
            super(colors, stops);
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        public static LinearGradient vertical(Color... colors) {
            double[] stops = new double[colors.length];
            for (int i = 0; i < colors.length; i++) {
                stops[i] = (double) i / (colors.length - 1);
            }
            return new LinearGradient(colors, stops, 0, 0, 0, 1);
        }

        public static LinearGradient horizontal(Color... colors) {
            double[] stops = new double[colors.length];
            for (int i = 0; i < colors.length; i++) {
                stops[i] = (double) i / (colors.length - 1);
            }
            return new LinearGradient(colors, stops, 0, 0, 1, 0);
        }

        public double getStartX() { return startX; }
        public double getStartY() { return startY; }
        public double getEndX() { return endX; }
        public double getEndY() { return endY; }
    }

    public static class RadialGradient extends Gradient {
        private final double centerX, centerY, radius;

        public RadialGradient(Color[] colors, double[] stops, double centerX, double centerY, double radius) {
            super(colors, stops);
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
        }

        public double getCenterX() { return centerX; }
        public double getCenterY() { return centerY; }
        public double getRadius() { return radius; }
    }

    public static class DecorationImage {
        private final String imagePath;
        private final BoxFit fit;
        private final Alignment alignment;

        public DecorationImage(String imagePath, BoxFit fit, Alignment alignment) {
            this.imagePath = imagePath;
            this.fit = fit;
            this.alignment = alignment;
        }

        public String getImagePath() { return imagePath; }
        public BoxFit getFit() { return fit; }
        public Alignment getAlignment() { return alignment; }

        public enum BoxFit {
            FILL, CONTAIN, COVER, FIT_WIDTH, FIT_HEIGHT, NONE, SCALE_DOWN
        }
    }

    public static class Alignment {
        public static final Alignment CENTER = new Alignment(0, 0);
        public static final Alignment TOP_LEFT = new Alignment(-1, -1);
        public static final Alignment TOP_CENTER = new Alignment(0, -1);
        public static final Alignment TOP_RIGHT = new Alignment(1, -1);
        public static final Alignment CENTER_LEFT = new Alignment(-1, 0);
        public static final Alignment CENTER_RIGHT = new Alignment(1, 0);
        public static final Alignment BOTTOM_LEFT = new Alignment(-1, 1);
        public static final Alignment BOTTOM_CENTER = new Alignment(0, 1);
        public static final Alignment BOTTOM_RIGHT = new Alignment(1, 1);

        private final double x, y;

        public Alignment(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() { return x; }
        public double getY() { return y; }
    }
}
