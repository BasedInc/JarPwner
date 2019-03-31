package me.zero.jarpwner.util;

import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Brady
 * @since 3/31/2019
 */
public final class InsnRange {

    private final AbstractInsnNode from;
    private final AbstractInsnNode to;

    private InsnRange(AbstractInsnNode from, AbstractInsnNode to) {
        this.from = from;
        this.to = to;
    }

    public AbstractInsnNode getFrom() {
        return this.from;
    }

    public AbstractInsnNode getTo() {
        return this.to;
    }

    public InsnRangeList getAll(boolean inclusive) {
        var current = this.from;
        var last    = this.to;

        if (inclusive) {
            last = last.getNext();
        }

        var nodes = new InsnRangeList();
        while (current != last) {
            nodes.add(current);
            current = current.getNext();

            // (from => to) was invalid
            if (current == null) {
                return null;
            }
        }

        return nodes;
    }

    public static InsnRange of(AbstractInsnNode from, AbstractInsnNode to) {
        return new InsnRange(from, to);
    }
}
