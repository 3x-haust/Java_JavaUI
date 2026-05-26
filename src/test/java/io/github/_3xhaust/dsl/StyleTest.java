package io.github._3xhaust.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Font;

import org.junit.jupiter.api.Test;

class StyleTest {
    @Test
    void fontSizeOnEmptyStyleCreatesSansFont() {
        Style s = new Style().fontSize(20);
        assertNotNull(s.getFont());
        assertEquals(20, s.getFont().getSize());
    }

    @Test
    void boldDerivesFromExisting() {
        Style s = new Style().font(new Font(Font.SANS_SERIF, Font.PLAIN, 14)).bold();
        assertTrue((s.getFont().getStyle() & Font.BOLD) != 0);
    }

    @Test
    void backgroundMakesOpaque() {
        Style s = new Style().backgroundColor(Color.RED);
        assertTrue(s.isOpaque());
        assertEquals(Color.RED, s.getBackgroundColor());
    }

    @Test
    void paddingAppliesToAllSides() {
        Style s = new Style().padding(8);
        assertEquals(8, s.getPadding().top);
        assertEquals(8, s.getPadding().left);
    }

    @Test
    void widthHeightCombineIntoSize() {
        Style s = new Style().width(100).height(50);
        assertEquals(100, s.getSize().width);
        assertEquals(50, s.getSize().height);
    }
}
