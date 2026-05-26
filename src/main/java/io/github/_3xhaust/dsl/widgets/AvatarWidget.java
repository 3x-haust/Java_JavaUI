package io.github._3xhaust.dsl.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingWorker;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.AbstractWidget;
import io.github._3xhaust.platform.swing.SwingRenderer;
import io.github._3xhaust.theme.ColorUtils;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.util.Fonts;
import io.github._3xhaust.util.ImageLoader;
import io.github._3xhaust.util.Logger;

/**
 * Circular avatar that renders either an image or initials over a colored disc.
 */
public final class AvatarWidget extends AbstractWidget<AvatarWidget> {
    private static final Logger LOG = Logger.get(AvatarWidget.class);

    private final String initials;
    private final String imageSrc;
    private int size = 40;
    private Color background;

    private AvatarWidget(String initials, String imageSrc) {
        this.initials = initials;
        this.imageSrc = imageSrc;
    }

    public static AvatarWidget Avatar(String initials) {
        return new AvatarWidget(initials, null);
    }

    public static AvatarWidget AvatarImage(String src) {
        return new AvatarWidget(null, src);
    }

    public AvatarWidget size(int v) { this.size = v; return this; }
    public AvatarWidget background(Color c) { this.background = c; return this; }

    @Override
    public List<View> getChildren() { return List.of(); }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        AvatarPane pane = new AvatarPane();
        SwingRenderer.applyStyles(pane, getStyle());
        swing.addComponent(pane);
    }

    private final class AvatarPane extends JComponent {
        private Image image;

        AvatarPane() {
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
            if (imageSrc != null) {
                new SwingWorker<Image, Void>() {
                    @Override
                    protected Image doInBackground() throws Exception {
                        return ImageLoader.load(imageSrc);
                    }

                    @Override
                    protected void done() {
                        try {
                            image = get();
                            repaint();
                        } catch (Exception e) {
                            LOG.warn("Avatar image load failed: " + e.getMessage());
                        }
                    }
                }.execute();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                Color bg = background != null ? background : ColorUtils.darken(Theme.get().primary(), 0.05);
                g2.setColor(bg);
                g2.fillOval(0, 0, w, h);
                if (image != null) {
                    g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, w, h));
                    g2.drawImage(image, 0, 0, w, h, null);
                } else if (initials != null) {
                    g2.setColor(ColorUtils.contrasting(bg));
                    Font f = new Font(Fonts.defaultSans(), Font.BOLD, Math.max(10, h / 2));
                    g2.setFont(f);
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = (w - fm.stringWidth(initials)) / 2;
                    int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(initials, tx, ty);
                }
            } finally {
                g2.dispose();
            }
        }
    }

}
