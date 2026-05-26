package io.github._3xhaust.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EdgeInsetsTest {
    @Test
    void allAppliesToEverySide() {
        EdgeInsets e = EdgeInsets.all(5);
        assertEquals(5, e.top);
        assertEquals(5, e.right);
        assertEquals(5, e.bottom);
        assertEquals(5, e.left);
    }

    @Test
    void symmetricMatchesPairs() {
        EdgeInsets e = EdgeInsets.symmetric(2, 4);
        assertEquals(2, e.top);
        assertEquals(2, e.bottom);
        assertEquals(4, e.left);
        assertEquals(4, e.right);
    }

    @Test
    void fromLTRBRespectsOrder() {
        EdgeInsets e = EdgeInsets.fromLTRB(1, 2, 3, 4);
        assertEquals(2, e.top);
        assertEquals(3, e.right);
        assertEquals(4, e.bottom);
        assertEquals(1, e.left);
    }

    @Test
    void zeroIsZero() {
        EdgeInsets e = EdgeInsets.zero();
        assertEquals(0, e.top + e.right + e.bottom + e.left);
    }

    @Test
    void awtConversionRoundsAndReorders() {
        EdgeInsets e = new EdgeInsets(1.4, 2.6, 3.5, 4.9);
        java.awt.Insets awt = e.toAwtInsets();
        assertEquals(1, awt.top);
        assertEquals(4, awt.left);
        assertEquals(3, awt.bottom);
        assertEquals(2, awt.right);
    }
}
