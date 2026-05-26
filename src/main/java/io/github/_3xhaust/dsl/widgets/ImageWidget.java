package io.github._3xhaust.dsl.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.util.ImageLoader;
import io.github._3xhaust.util.Logger;

/**
 * Loads an image from a file path, classpath ({@code classpath:foo.png}) or URL.
 * SVGs are rasterized via SVG Salamander. HTTP loads honor a configurable timeout.
 */
public final class ImageWidget extends AbstractWidget<ImageWidget> {
    private static final Logger LOG = Logger.get(ImageWidget.class);

    private final String src;
    private double width = -1;
    private double height = -1;
    private int timeoutMs = ImageLoader.DEFAULT_TIMEOUT_MS;

    public ImageWidget(String src) {
        this.src = src;
    }

    public static ImageWidget Image(String src) {
        return new ImageWidget(src);
    }

    public ImageWidget width(double w) { this.width = w; return this; }
    public ImageWidget height(double h) { this.height = h; return this; }
    public ImageWidget size(double w, double h) { this.width = w; this.height = h; return this; }
    public ImageWidget timeoutMs(int v) { this.timeoutMs = v; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        if (width > 0 && height > 0) {
            label.setPreferredSize(new Dimension((int) width, (int) height));
        }

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    Image image = ImageLoader.load(src, timeoutMs);
                    if (image == null) return placeholder("not found");
                    if (width > 0 && height > 0) {
                        image = image.getScaledInstance((int) width, (int) height, Image.SCALE_SMOOTH);
                    }
                    return new ImageIcon(image);
                } catch (Exception e) {
                    LOG.warn("Image load failed (" + src + "): " + e.getMessage());
                    return placeholder(e.getMessage());
                }
            }

            @Override
            protected void done() {
                try {
                    label.setIcon(get());
                    if (width <= 0 || height <= 0) {
                        ImageIcon icon = (ImageIcon) label.getIcon();
                        label.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
                    }
                    label.revalidate();
                    label.repaint();
                } catch (Exception e) {
                    LOG.warn("Image swap failed: " + e.getMessage());
                }
            }
        }.execute();

        SwingRenderer.applyStyles(label, getStyle());
        swing.addComponent(label);
    }

    private ImageIcon placeholder(String text) {
        int w = width > 0 ? (int) width : 100;
        int h = height > 0 ? (int) height : 100;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(0, 0, w - 1, h - 1);
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        String label = text == null || text.isEmpty() ? "image" : text;
        int sw = fm.stringWidth(label);
        g.drawString(label, Math.max(2, (w - sw) / 2), h / 2 + fm.getAscent() / 2);
        g.dispose();
        return new ImageIcon(img);
    }
}
