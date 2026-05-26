package io.github._3xhaust.dsl.widgets;

import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.Timer;

/**
 * Minimal value-tween helper. Animates a {@code double} from {@code from} to
 * {@code to} over a duration, invoking the consumer on each frame.
 *
 * <pre>
 *   Animation.tween(0.0, 1.0, 250, value -> {
 *       opacity.set(value);
 *   });
 * </pre>
 */
public final class Animation {
    private Animation() {}

    public static Timer tween(double from, double to, int durationMs, Consumer<Double> onFrame) {
        return tween(from, to, durationMs, Easing.LINEAR, onFrame, null);
    }

    public static Timer tween(double from, double to, int durationMs,
                              Easing easing, Consumer<Double> onFrame, Runnable onDone) {
        long start = System.currentTimeMillis();
        Timer timer = new Timer(16, null);
        ActionListener tick = e -> {
            long elapsed = System.currentTimeMillis() - start;
            double t = Math.min(1.0, durationMs <= 0 ? 1.0 : (double) elapsed / durationMs);
            double eased = easing.apply(t);
            double v = from + (to - from) * eased;
            onFrame.accept(v);
            if (t >= 1.0) {
                timer.stop();
                if (onDone != null) onDone.run();
            }
        };
        timer.addActionListener(tick);
        timer.start();
        return timer;
    }

    public enum Easing {
        LINEAR {
            @Override public double apply(double t) { return t; }
        },
        EASE_IN {
            @Override public double apply(double t) { return t * t; }
        },
        EASE_OUT {
            @Override public double apply(double t) { return 1 - (1 - t) * (1 - t); }
        },
        EASE_IN_OUT {
            @Override public double apply(double t) {
                return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
            }
        },
        BOUNCE {
            @Override public double apply(double t) {
                if (t < 1 / 2.75) return 7.5625 * t * t;
                if (t < 2 / 2.75) { t -= 1.5 / 2.75; return 7.5625 * t * t + 0.75; }
                if (t < 2.5 / 2.75) { t -= 2.25 / 2.75; return 7.5625 * t * t + 0.9375; }
                t -= 2.625 / 2.75; return 7.5625 * t * t + 0.984375;
            }
        };

        public abstract double apply(double t);
    }
}
