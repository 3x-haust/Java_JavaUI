package io.github._3xhaust.core;

import io.github._3xhaust.core.ir.IR;
import io.github._3xhaust.dsl.enums.CrossAxisAlignment;
import io.github._3xhaust.dsl.enums.MainAxisAlignment;
import io.github._3xhaust.state.State;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Renderer implementation that does not paint directly but builds a binary IR patch
 * to be consumed by the native engine. This is a first cut, mapping the existing
 * Renderer API to IR primitives with minimal properties.
 */
public class IRRenderer implements Renderer {
    // Node type keys (sync with native engine later)
    private static final int NODE_COLUMN = 1;
    private static final int NODE_ROW = 2;
    private static final int NODE_CENTER = 3;
    private static final int NODE_SIZED_BOX = 4;
    private static final int NODE_TEXT = 5;
    private static final int NODE_BUTTON = 6;

    // Property keys (provisional)
    private static final int PROP_MAIN_ALIGN = 101;
    private static final int PROP_CROSS_ALIGN = 102;
    private static final int PROP_GAP = 103;
    private static final int PROP_PADDING_LTRB = 104; // packed int (8bit each * 4) or four ints later
    private static final int PROP_TEXT = 201;
    private static final int PROP_SIZE_W = 301;
    private static final int PROP_SIZE_H = 302;

    private final IR.Builder builder;
    private final AtomicInteger nodeIdSeq = new AtomicInteger(1);
    private final Deque<Integer> parentStack = new ArrayDeque<>();
    private Integer rootId = null;

    public IRRenderer() {
        // 64KB initial patch buffer, will adjust later
        this.builder = new IR.Builder(64 * 1024);
    }

    public ByteBuffer buildPatch() {
        return builder.build();
    }

    private int newNode(int type) {
        int id = nodeIdSeq.getAndIncrement();
        builder.createNode(id, type);
        if (parentStack.peek() != null) {
            builder.appendChild(parentStack.peek(), id);
        } else if (rootId == null) {
            rootId = id;
        }
        return id;
    }

    private static int packPadding(Insets p) {
        if (p == null) return 0;
        int l = Math.max(0, p.left) & 0xFF;
        int t = Math.max(0, p.top) & 0xFF;
        int r = Math.max(0, p.right) & 0xFF;
        int b = Math.max(0, p.bottom) & 0xFF;
        return (t << 24) | (r << 16) | (b << 8) | l;
    }

    private static int alignToInt(MainAxisAlignment m) {
        return m != null ? m.ordinal() : 0;
    }

    private static int alignToInt(CrossAxisAlignment c) {
        return c != null ? c.ordinal() : 0;
    }

    @Override
    public void init(String title, int width, int height) {
        // no-op for IR building
    }

    @Override
    public void mount(View root) {
        // Build from scratch for now
        if (root != null) root.render(this);
    }

    @Override
    public void update(View oldView, View newView) {
        // TODO: implement diff-based updates
        parentStack.clear();
        nodeIdSeq.set(1);
        rootId = null;
        if (newView != null) newView.render(this);
    }

    @Override
    public void unmount(View view) {
        // TODO: emit deletions
    }

    @Override
    public void pushColumn(MainAxisAlignment mainAxisAlignment, CrossAxisAlignment crossAxisAlignment, Insets padding, int gap) {
        int id = newNode(NODE_COLUMN);
        builder.setPropInt(id, PROP_MAIN_ALIGN, alignToInt(mainAxisAlignment));
        builder.setPropInt(id, PROP_CROSS_ALIGN, alignToInt(crossAxisAlignment));
        builder.setPropInt(id, PROP_GAP, gap);
        builder.setPropInt(id, PROP_PADDING_LTRB, packPadding(padding));
        parentStack.push(id);
    }

    @Override
    public void pushRow(MainAxisAlignment mainAxisAlignment, CrossAxisAlignment crossAxisAlignment, Insets padding, int gap) {
        int id = newNode(NODE_ROW);
        builder.setPropInt(id, PROP_MAIN_ALIGN, alignToInt(mainAxisAlignment));
        builder.setPropInt(id, PROP_CROSS_ALIGN, alignToInt(crossAxisAlignment));
        builder.setPropInt(id, PROP_GAP, gap);
        builder.setPropInt(id, PROP_PADDING_LTRB, packPadding(padding));
        parentStack.push(id);
    }

    @Override
    public void pushCenter() {
        int id = newNode(NODE_CENTER);
        parentStack.push(id);
    }

    @Override
    public void pushSizedBox(int width, int height) {
        int id = newNode(NODE_SIZED_BOX);
        builder.setPropInt(id, PROP_SIZE_W, width);
        builder.setPropInt(id, PROP_SIZE_H, height);
        parentStack.push(id);
    }

    @Override
    public void pop() {
        if (!parentStack.isEmpty()) parentStack.pop();
    }

    @Override
    public void addText(String text) {
        int id = newNode(NODE_TEXT);
        builder.setPropString(id, PROP_TEXT, text != null ? text : "");
    }

    @Override
    public <T> void addText(State<T> state) {
        int id = newNode(NODE_TEXT);
        builder.setPropString(id, PROP_TEXT, state != null && state.get() != null ? state.get().toString() : "");
        // TODO: subscribe for future diffs
    }

    @Override
    public void addButton(String text, Runnable onClick) {
        int id = newNode(NODE_BUTTON);
        builder.setPropString(id, PROP_TEXT, text != null ? text : "");
        // TODO: wire onClick via nodeId → event dispatch
    }
}


