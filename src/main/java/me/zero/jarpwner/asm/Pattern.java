package me.zero.jarpwner.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.objectweb.asm.tree.AbstractInsnNode.*;

/**
 * @author Brady
 * @since 3/31/2019
 */
public final class Pattern {

    private final Integer[] opcodes;

    private Pattern(Integer... opcodes) {
        this.opcodes = opcodes;
    }

    /**
     * Finds all matches to this pattern in the specified instruction list, which are expressed as {@link InsnSlice}s.
     * The {@link InsnSlice} begins at the pattern start instruction and ends at the pattern end instruction,
     * inclusively.
     *
     * @see SearchFlags
     * @see InsnSlice
     *
     * @param list The instruction list to search for this pattern
     * @param flags The {@link SearchFlags} that define which instructions to ignore
     * @return All of the pattern matches
     */
    public final List<InsnSlice> find(InsnList list, int flags) {
        return this.find(list, insn -> {
            switch (insn.getType()) {
                case LABEL: {
                    return (flags & SearchFlags.IGNORE_LABELS) != 0;
                }
                case FRAME: {
                    return (flags & SearchFlags.IGNORE_FRAMES) != 0;
                }
                case LINE: {
                    return (flags & SearchFlags.IGNORE_LINES) != 0;
                }
            }
            return false;
        });
    }

    /**
     * Finds all matches to this pattern in the specified instruction list, which are expressed as {@link InsnSlice}s.
     * The {@link InsnSlice} begins at the pattern start instruction and ends at the pattern end instruction,
     * inclusively. The specified {@link Predicate} is used to determine whether instructions being read through should
     * be ignored, this is more commonly used with the {@link #find(InsnList, int)} method, where the predicate is defined
     * by the {@link SearchFlags} present.
     *
     * @see InsnSlice
     *
     * @param list The instruction list to search for this pattern
     * @param ignoreInsn A predicate used to determine which instructions to ignore which intersect with the pattern
     * @return All of the pattern matches
     */
    public final List<InsnSlice> find(InsnList list, Predicate<AbstractInsnNode> ignoreInsn) {
        var matches = new ArrayList<InsnSlice>();

        outer:
        for (int i = 0; i <= list.size() - this.opcodes.length; i++) {
            var last = i; // The last matched instruction

            int offset = 0; // The current instruction offset from the base index
            int matched = 0; // The number of matched instructions
            while (matched < this.opcodes.length) {
                var index = i + offset++;

                // If we've reached an index that exceeds the size of the instruction list, then restart the search
                if (index >= list.size()) {
                    continue outer;
                }

                var insn = list.get(index);

                // First instruction MUST be what we're looking for, only apply the ignore check once we're past it
                if (matched > 0 && ignoreInsn.test(insn)) {
                    continue;
                }

                // Determine if the found opcode matches the target one
                if (this.opcodes[matched] != null && insn.getOpcode() != this.opcodes[matched]) {
                    continue outer;
                }

                last = index;
                matched++;
            }

            matches.add(InsnSlice.of(list, i, last));
        }

        return matches;
    }

    /**
     * @return An array of this pattern's opcodes
     */
    public final Integer[] getOpcodes() {
        return this.opcodes.clone();
    }

    /**
     * @return A stream of this pattern's opcodes
     */
    public final Stream<Integer> stream() {
        return Stream.of(this.opcodes);
    }

    /**
     * @param opcodes An array of opcodes
     * @return A pattern from the opcodes
     */
    public static Pattern of(Integer... opcodes) {
        return new Pattern(opcodes);
    }

    /**
     * @param opcodes A stream of opcodes
     * @return A pattern from the opcodes
     */
    public static Pattern of(Stream<Integer> opcodes) {
        return new Pattern(opcodes.toArray(Integer[]::new));
    }

    /**
     * @param opcodes A list of opcodes
     * @return A pattern from the opcodes
     */
    public static Pattern of(List<Integer> opcodes) {
        return new Pattern(opcodes.toArray(new Integer[0]));
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public interface SearchFlags {

        /**
         * Ignores instructions with the type {@link AbstractInsnNode#LABEL}
         */
        int IGNORE_LABELS = 1 << 0;

        /**
         * Ignores instructions with the type {@link AbstractInsnNode#FRAME}
         */
        int IGNORE_FRAMES = 1 << 1;

        /**
         * Ignores instructions with the type {@link AbstractInsnNode#LINE}
         */
        int IGNORE_LINES  = 1 << 2;

        /**
         * Ignores instructions with the type {@link AbstractInsnNode#LABEL}, {@link AbstractInsnNode#FRAME}, and {@link AbstractInsnNode#LINE}
         */
        int IGNORE_ALL = IGNORE_LABELS | IGNORE_FRAMES | IGNORE_LINES;
    }
}
