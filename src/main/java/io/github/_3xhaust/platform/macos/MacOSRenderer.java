package io.github._3xhaust.platform.macos;

import io.github._3xhaust.core.Renderer;
import io.github._3xhaust.platform.swing.SwingRenderer;

/**
 * macOS 데스크톱 기본 Renderer. 현재 SwingRenderer를 래핑하여 OS 기준 네임스페이스를 제공.
 * 향후 macOS 전용 JavaFX 렌더러 또는 네이티브 브릿지로 교체 가능.
 */
public class MacOSRenderer extends SwingRenderer implements Renderer {
}


