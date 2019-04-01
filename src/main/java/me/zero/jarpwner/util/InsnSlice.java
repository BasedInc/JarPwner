package me.zero.jarpwner.util;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

/**
 * @author Brady
 * @since 3/31/2019
 */
public final class InsnSlice {

    private final AbstractInsnNode from;
    private final AbstractInsnNode to;

    private InsnSlice(AbstractInsnNode from, AbstractInsnNode to) {
        this.from = from;
        this.to = to;
    }

    public final AbstractInsnNode getFrom() {
        return this.from;
    }

    public final AbstractInsnNode getTo() {
        return this.to;
    }

    public final InsnListSearchable getAll(boolean inclusive) {
        var current = this.from;
        var last    = this.to;

        if (inclusive) {
            last = last.getNext();
        }

        var nodes = new InsnListSearchable();
        while (current != last) {
            nodes.add(current);
            current = current.getNext();

            // (from => to) was invalid
            if (current == null && last != null) {
                return null;
            }
        }

        return nodes;
    }

    public final boolean delete(InsnList list) {
        var insns = this.getAll(true);
        if (insns == null) {
            return false;
        }
        insns.forEach(insn -> {
            if (insn.getOpcode() >= 0) {
                list.remove(insn);
            }
        });
        return true;
    }

    public static InsnSlice of(AbstractInsnNode from, AbstractInsnNode to) {
        return new InsnSlice(from, to);
    }
}
