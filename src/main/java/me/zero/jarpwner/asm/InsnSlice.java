package me.zero.jarpwner.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

/**
 * An instruction slice is a range of instructions
 *
 * @author Brady
 * @since 3/31/2019
 */
public final class InsnSlice {

    /**
     * The starting instruction node
     */
    private final AbstractInsnNode from;

    /**
     * The ending instruction node
     */
    private final AbstractInsnNode to;

    private InsnSlice(AbstractInsnNode from, AbstractInsnNode to) {
        this.from = from;
        this.to = to;
    }

    /**
     * @return The starting instruction node of this slice
     */
    public final AbstractInsnNode getFrom() {
        return this.from;
    }

    /**
     * @return The ending instruction node of this slice
     */
    public final AbstractInsnNode getTo() {
        return this.to;
    }

    /**
     * Returns an {@link InsnListSearchable} that contains all of the instructions from this slice's
     * first, to the last, depending on what the state of the {@code inclusive} parameter is.
     *
     * @param inclusive Whether or not to include the last instruction of this slice in the list
     * @return All instructions present in this slice
     */
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

    /**
     * Deletes all of the instructions of this slice from the specified instruction list.
     *
     * @param list The instruction list to delete from
     * @param inclusive Whether or not to include the last instruction in this slice
     * @return If the deletion was successful, it should
     */
    public final boolean delete(InsnList list, boolean inclusive) {
        var insns = this.getAll(inclusive);
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

    /**
     * Creates an {@link InsnSlice} from a given starting and ending instruction node
     *
     * @param from The starting instruction
     * @param to The ending instruction
     * @return A slice defining the range from start to end
     */
    public static InsnSlice of(AbstractInsnNode from, AbstractInsnNode to) {
        InsnSlice slice = new InsnSlice(from, to);

        // There must be a valid flow from the first to last instruction
        if (slice.getAll(true) == null) {
            throw new IllegalArgumentException("Unable to complete path from first to last instruction");
        }

        return slice;
    }

    /**
     * Creates an {@link InsnSlice} from an instruction list and the starting and ending indices
     *
     * @param from The starting instruction
     * @param to The ending instruction
     * @return A slice defining the range from start to end
     */
    public static InsnSlice of(InsnList list, int from, int to) {
        if (to < from) {
            throw new IllegalArgumentException("The ending index must be larger than the starting one");
        }

        return of(list.get(from), list.get(to));
    }
}
