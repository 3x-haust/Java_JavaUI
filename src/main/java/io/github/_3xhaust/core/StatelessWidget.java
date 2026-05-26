package io.github._3xhaust.core;

/**
 * A widget whose appearance depends only on the values passed into its constructor.
 * Subclasses implement {@link #build()} and let the framework call {@link View#render(Renderer)}.
 */
public abstract class StatelessWidget implements View {
    public abstract View build();

    @Override
    public final void render(Renderer renderer) {
        View built = build();
        if (built == null) return;
        built.render(renderer);
    }
}
