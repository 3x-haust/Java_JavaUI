package io.github._3xhaust.core;

import java.nio.ByteBuffer;

/**
 * Minimal JNI bridge stubs for the custom UI engine. These methods will be backed by
 * a native shared library (libjui_engine) once the engine is built. Until then, they
 * fail fast if invoked when the native library isn't available.
 */
public final class EngineBridge {
    private static final boolean nativeAvailable;

    static {
        boolean loaded = false;
        // Priority 1: explicit path via system property
        String explicit = System.getProperty("jui.engine.lib");
        if (explicit == null || explicit.isBlank()) {
            explicit = System.getenv("JUI_ENGINE_LIB");
        }
        if (explicit != null && !explicit.isBlank()) {
            try {
                System.load(explicit);
                loaded = true;
            } catch (UnsatisfiedLinkError ignored) {
                loaded = false;
            }
        }
        // Priority 2: library path resolution
        if (!loaded) {
            try {
                System.loadLibrary("jui_engine");
                loaded = true;
            } catch (UnsatisfiedLinkError ignore) {
                loaded = false;
            }
        }
        nativeAvailable = loaded;
    }

    private EngineBridge() {}

    public static boolean isNativeAvailable() {
        return nativeAvailable;
    }

    private static void ensureNative() {
        if (!nativeAvailable) {
            throw new UnsupportedOperationException("Native engine library (jui_engine) is not loaded yet.");
        }
    }

    // Lifecycle
    public static long engineCreate(int width, int height, int deviceType, long nativeSurfacePtr) {
        ensureNative();
        return nEngineCreate(width, height, deviceType, nativeSurfacePtr);
    }

    public static void engineDestroy(long handle) {
        ensureNative();
        nEngineDestroy(handle);
    }

    // Frame & patches
    public static void engineApplyPatches(long handle, ByteBuffer patches) {
        ensureNative();
        nEngineApplyPatches(handle, patches, patches != null ? patches.remaining() : 0);
    }

    public static void engineBeginFrame(long handle, double frameTimeNanos) {
        ensureNative();
        nEngineBeginFrame(handle, frameTimeNanos);
    }

    // Input
    public static void engineDispatchPointer(long handle, int action, float x, float y, int buttons) {
        ensureNative();
        nEngineDispatchPointer(handle, action, x, y, buttons);
    }

    public static void engineDispatchKey(long handle, int keyCode, boolean down, int modifiers) {
        ensureNative();
        nEngineDispatchKey(handle, keyCode, down ? 1 : 0, modifiers);
    }

    // --- Native declarations ---
    private static native long nEngineCreate(int width, int height, int deviceType, long nativeSurfacePtr);
    private static native void nEngineDestroy(long handle);
    private static native void nEngineApplyPatches(long handle, ByteBuffer patches, int size);
    private static native void nEngineBeginFrame(long handle, double frameTimeNanos);
    private static native void nEngineDispatchPointer(long handle, int action, float x, float y, int buttons);
    private static native void nEngineDispatchKey(long handle, int keyCode, int down, int modifiers);
}


