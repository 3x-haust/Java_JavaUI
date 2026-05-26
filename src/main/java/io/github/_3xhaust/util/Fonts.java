package io.github._3xhaust.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Cross-platform font resolver. Picks the first installed font from a preference list,
 * falling back to {@link Font#SANS_SERIF} so the framework never crashes on missing fonts.
 */
public final class Fonts {
    private Fonts() {}

    private static final Set<String> AVAILABLE = installedFamilies();
    private static final String[] PREFERRED_SANS = {
            "Inter", "SF Pro Display", "Helvetica Neue", "Helvetica",
            "Segoe UI", "Roboto", "Arial", "Pretendard", "Apple SD Gothic Neo",
            "Malgun Gothic", "Noto Sans CJK KR", "Noto Sans"
    };
    private static final String[] PREFERRED_MONO = {
            "JetBrains Mono", "SF Mono", "Menlo", "Consolas", "Cascadia Mono",
            "Source Code Pro", "Roboto Mono", "Courier New"
    };

    private static Set<String> installedFamilies() {
        try {
            return new HashSet<>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        } catch (Throwable t) {
            return new HashSet<>();
        }
    }

    public static String defaultSans() {
        for (String f : PREFERRED_SANS) {
            if (AVAILABLE.contains(f)) return f;
        }
        return Font.SANS_SERIF;
    }

    public static String defaultMono() {
        for (String f : PREFERRED_MONO) {
            if (AVAILABLE.contains(f)) return f;
        }
        return Font.MONOSPACED;
    }

    public static Font sans(int style, int size) {
        return new Font(defaultSans(), style, size);
    }

    public static Font sans(int size) {
        return sans(Font.PLAIN, size);
    }

    public static Font mono(int style, int size) {
        return new Font(defaultMono(), style, size);
    }
}
