package io.github._3xhaust.dsl.widgets;

import java.awt.Dimension;

import javax.swing.Box;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.core.View;
import io.github._3xhaust.platform.swing.SwingRenderer;

/**
 * Flexible empty space that pushes siblings apart inside Row/Column.
 * For a fixed-size gap use {@link SizedBoxWidget}.
 */
public final class SpacerWidget implements View {
    private int flex = 1;

    public static SpacerWidget Spacer() {
        return new SpacerWidget();
    }

    public static SpacerWidget Spacer(int flex) {
        SpacerWidget s = new SpacerWidget();
        s.flex = Math.max(1, flex);
        return s;
    }

    public int flex() {
        return flex;
    }

    @Override
    public void render(Renderer renderer) {
        if (!(renderer instanceof SwingRenderer swing)) return;
        Box.Filler glue = (Box.Filler) Box.createGlue();
        glue.changeShape(new Dimension(0, 0), new Dimension(0, 0),
                new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        swing.addComponent(glue);
    }
}
