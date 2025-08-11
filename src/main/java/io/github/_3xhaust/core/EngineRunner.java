package io.github._3xhaust.core;

import java.nio.ByteBuffer;

public final class EngineRunner implements AutoCloseable {
    private final long handle;
    private volatile boolean running = false;

    public EngineRunner(int width, int height, int deviceType, long nativeSurfacePtr) {
        if (!EngineBridge.isNativeAvailable()) {
            throw new IllegalStateException("Native engine not available");
        }
        this.handle = EngineBridge.engineCreate(width, height, deviceType, nativeSurfacePtr);
    }

    public void applyPatch(ByteBuffer patch) {
        EngineBridge.engineApplyPatches(handle, patch);
    }

    public void runFrames(int frames, int fps) {
        running = true;
        long nanosPerFrame = 1_000_000_000L / Math.max(1, fps);
        long t0 = System.nanoTime();
        for (int i = 0; i < frames && running; i++) {
            long tn = System.nanoTime();
            EngineBridge.engineBeginFrame(handle, (double) tn);
            long target = t0 + (i + 1) * nanosPerFrame;
            long sleepNanos = target - System.nanoTime();
            if (sleepNanos > 0) {
                try { Thread.sleep(sleepNanos / 1_000_000L, (int) (sleepNanos % 1_000_000L)); } catch (InterruptedException ignored) {}
            }
        }
        running = false;
    }

    public void stop() { running = false; }

    @Override
    public void close() {
        EngineBridge.engineDestroy(handle);
    }
}


