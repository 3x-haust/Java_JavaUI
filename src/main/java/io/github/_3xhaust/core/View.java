package io.github._3xhaust.core;

/**
 * The atomic unit of the UI tree. Anything that can be rendered to a {@link Renderer}
 * (widgets, custom components, stateful trees) implements this interface.
 */
public interface View {
    void render(Renderer renderer);
}
