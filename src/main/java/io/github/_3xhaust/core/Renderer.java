package io.github._3xhaust.core;

import java.awt.Component;
import java.awt.Container;

/**
 * Platform-neutral rendering primitive. The framework currently ships a Swing
 * implementation in {@code io.github._3xhaust.platform.swing.SwingRenderer}.
 */
public interface Renderer {
    void init(String title, int width, int height);

    /** Replace the entire mounted view tree with the given root. */
    void mount(View root);

    /** Optional: incremental update hook (no-op for the Swing backend). */
    default void update(View oldView, View newView) {}

    /** Optional: detach hook called when the root view is being replaced. */
    default void unmount(View view) {}

    /** Schedule a rebuild on the next render frame. */
    void requestRebuild();

    /** Push a Swing-style container onto the build stack. */
    void pushContainer(Container container);

    /** Pop the most recently pushed container. */
    void popContainer();

    /** Add a component to the top container on the stack. */
    void addComponent(Component comp);
}
