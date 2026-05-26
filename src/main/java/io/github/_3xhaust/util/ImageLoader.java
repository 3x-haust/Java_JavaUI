package io.github._3xhaust.util;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Loads images from classpath resources, file system or http(s) URLs.
 * HTTP(S) loads honor connect/read timeouts so the EDT cannot stall forever.
 */
public final class ImageLoader {
    private static final Logger LOG = Logger.get(ImageLoader.class);
    public static final int DEFAULT_TIMEOUT_MS = 7000;

    private ImageLoader() {}

    public static Image load(String src) throws Exception {
        return load(src, DEFAULT_TIMEOUT_MS);
    }

    public static Image load(String src, int timeoutMs) throws Exception {
        if (src == null || src.isBlank()) {
            throw new IllegalArgumentException("Image src is null/blank");
        }
        String lower = src.toLowerCase();
        if (lower.endsWith(".svg")) {
            return loadSvg(src, timeoutMs);
        }
        if (src.startsWith("http://") || src.startsWith("https://")) {
            return loadHttp(src, timeoutMs);
        }
        if (src.startsWith("classpath:")) {
            return loadClasspath(src.substring("classpath:".length()));
        }
        File f = new File(src);
        if (f.exists()) return ImageIO.read(f);
        // fall back: try classpath
        Image cp = tryClasspath(src);
        if (cp != null) return cp;
        throw new IllegalStateException("Image not found: " + src);
    }

    private static Image loadSvg(String src, int timeoutMs) throws Exception {
        SVGUniverse universe = new SVGUniverse();
        URI uri;
        if (src.startsWith("http://") || src.startsWith("https://")) {
            uri = URI.create(src);
            universe.loadSVG(uri.toURL());
        } else if (src.startsWith("classpath:")) {
            String path = src.substring("classpath:".length());
            try (InputStream in = ImageLoader.class.getClassLoader().getResourceAsStream(path)) {
                if (in == null) throw new IllegalStateException("SVG not found on classpath: " + path);
                uri = universe.loadSVG(in, path);
            }
        } else {
            File file = new File(src);
            uri = file.toURI();
            universe.loadSVG(uri.toURL());
        }
        SVGDiagram d = universe.getDiagram(uri);
        if (d == null) throw new IllegalStateException("SVG did not parse: " + src);
        int w = Math.max(1, (int) d.getWidth());
        int h = Math.max(1, (int) d.getHeight());
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        d.render(g);
        g.dispose();
        return img;
    }

    private static Image loadHttp(String src, int timeoutMs) throws Exception {
        URL url = URI.create(src).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(timeoutMs);
        conn.setReadTimeout(timeoutMs);
        conn.setRequestProperty("User-Agent", "JavaUI/0.2");
        try (InputStream in = conn.getInputStream()) {
            return ImageIO.read(in);
        } finally {
            conn.disconnect();
        }
    }

    private static Image loadClasspath(String path) throws Exception {
        try (InputStream in = ImageLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) throw new IllegalStateException("Resource not found: " + path);
            return ImageIO.read(in);
        }
    }

    private static Image tryClasspath(String path) {
        try (InputStream in = ImageLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) return null;
            return ImageIO.read(in);
        } catch (Exception e) {
            LOG.debug("classpath image lookup failed for " + path + ": " + e.getMessage());
            return null;
        }
    }
}
