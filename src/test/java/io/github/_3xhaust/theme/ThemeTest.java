package io.github._3xhaust.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ThemeTest {
    @AfterEach
    void resetTheme() {
        Theme.set(ThemeData.light());
    }

    @Test
    void lightThemeIsNotDark() {
        Theme.set(ThemeData.light());
        assertFalse(Theme.get().isDark());
    }

    @Test
    void darkThemeIsDark() {
        Theme.set(ThemeData.dark());
        assertTrue(Theme.get().isDark());
    }

    @Test
    void toggleSwitches() {
        Theme.set(ThemeData.light());
        Theme.toggleDark();
        assertTrue(Theme.get().isDark());
        Theme.toggleDark();
        assertFalse(Theme.get().isDark());
    }

    @Test
    void subscribersFireOnSet() throws Exception {
        AtomicReference<ThemeMode> latest = new AtomicReference<>();
        AutoCloseable token = Theme.subscribe(t -> latest.set(t.mode()));
        Theme.set(ThemeData.dark());
        assertEquals(ThemeMode.DARK, latest.get());
        token.close();
    }

    @Test
    void builderPropagatesValues() {
        ThemeData td = ThemeData.light().toBuilder().radius(16).build();
        assertEquals(16, td.radius());
    }
}
