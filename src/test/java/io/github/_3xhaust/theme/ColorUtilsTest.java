package io.github._3xhaust.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class ColorUtilsTest {
    @Test
    void parsesHex6() {
        Color c = ColorUtils.fromHex("#FF8800");
        assertEquals(255, c.getRed());
        assertEquals(136, c.getGreen());
        assertEquals(0, c.getBlue());
    }

    @Test
    void parsesShortHex() {
        Color c = ColorUtils.fromHex("#F80");
        assertEquals(255, c.getRed());
        assertEquals(136, c.getGreen());
        assertEquals(0, c.getBlue());
    }

    @Test
    void parsesHex8WithAlpha() {
        Color c = ColorUtils.fromHex("#FF8800AA");
        assertEquals(170, c.getAlpha());
    }

    @Test
    void invalidHexThrows() {
        assertThrows(IllegalArgumentException.class, () -> ColorUtils.fromHex("nope"));
    }

    @Test
    void withAlphaClamps() {
        Color c = ColorUtils.withAlpha(Color.RED, 9999);
        assertEquals(255, c.getAlpha());
        Color c2 = ColorUtils.withAlpha(Color.RED, -10);
        assertEquals(0, c2.getAlpha());
    }

    @Test
    void lightenMovesTowardWhite() {
        Color l = ColorUtils.lighten(Color.RED, 0.5);
        assertTrue(l.getGreen() > 0);
        assertTrue(l.getBlue() > 0);
    }

    @Test
    void darkenMovesTowardBlack() {
        Color d = ColorUtils.darken(Color.RED, 0.5);
        assertTrue(d.getRed() < 255);
    }

    @Test
    void contrastingPicksWhiteForDark() {
        assertEquals(Color.WHITE, ColorUtils.contrasting(new Color(20, 20, 20)));
    }

    @Test
    void contrastingPicksBlackForLight() {
        assertEquals(Color.BLACK, ColorUtils.contrasting(new Color(240, 240, 240)));
    }

    @Test
    void roundTripHex() {
        Color in = new Color(12, 34, 56, 78);
        Color out = ColorUtils.fromHex(ColorUtils.toHex(in));
        assertEquals(in, out);
        assertNotNull(out);
    }
}
