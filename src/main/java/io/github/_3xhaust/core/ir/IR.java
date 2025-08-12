package io.github._3xhaust.core.ir;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Minimal binary IR builder for view tree patches.
 * Format (little-endian):
 * [u8 version=1][u8 reserved=0][u32 length][sequence of messages]
 * Message: [u8 opcode][payload...]
 */
public final class IR {
    public static final byte VERSION = 1;

    public enum Op {
        CREATE_NODE(1), DELETE_NODE(2), SET_PROP(3), APPEND_CHILD(4), INSERT_CHILD(5), REMOVE_CHILD(6), REPLACE_CHILD(7),
        SET_PROP_STR(8);
        public final byte code;
        Op(int c) { this.code = (byte) c; }
    }

    public static final class Builder {
        private final ByteBuffer buf;

        public Builder(int capacity) {
            this.buf = ByteBuffer.allocateDirect(capacity).order(ByteOrder.LITTLE_ENDIAN);
            // header placeholder
            buf.put(VERSION).put((byte) 0).putInt(0);
        }

        public Builder createNode(int nodeId, int nodeType) {
            buf.put(Op.CREATE_NODE.code).putInt(nodeId).putInt(nodeType);
            return this;
        }

        public Builder deleteNode(int nodeId) {
            buf.put(Op.DELETE_NODE.code).putInt(nodeId);
            return this;
        }

        public Builder setProp(int nodeId, int propKey, float value) {
            buf.put(Op.SET_PROP.code).putInt(nodeId).putInt(propKey).putFloat(value);
            return this;
        }

        public Builder setPropInt(int nodeId, int propKey, int value) {
            buf.put(Op.SET_PROP.code).putInt(nodeId).putInt(propKey).putInt(value);
            return this;
        }

        public Builder setPropColor(int nodeId, int argb) {
            buf.put(Op.SET_PROP.code).putInt(nodeId).putInt(0x7FFF0001).putInt(argb); // example color key
            return this;
        }

        public Builder appendChild(int parentId, int childId) {
            buf.put(Op.APPEND_CHILD.code).putInt(parentId).putInt(childId);
            return this;
        }

        public Builder setPropString(int nodeId, int propKey, String value) {
            byte[] bytes = value != null ? value.getBytes(java.nio.charset.StandardCharsets.UTF_8) : new byte[0];
            buf.put(Op.SET_PROP_STR.code).putInt(nodeId).putInt(propKey).putInt(bytes.length).put(bytes);
            return this;
        }

        public ByteBuffer build() {
            int end = buf.position();
            buf.putInt(2, end - 6); // write length at header[2..6)
            buf.position(0);
            buf.limit(end);
            return buf.slice().order(ByteOrder.LITTLE_ENDIAN);
        }
    }
}


