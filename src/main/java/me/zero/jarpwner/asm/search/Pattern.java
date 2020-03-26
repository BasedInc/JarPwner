package me.zero.jarpwner.asm.search;

import me.zero.jarpwner.asm.InsnSlice;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static me.zero.jarpwner.asm.search.PatternUtils.*;
import static org.objectweb.asm.tree.AbstractInsnNode.*;

/**
 * @author Brady
 * @since 3/31/2019
 */
public final class Pattern {

    private final InsnPredicate[] checks;

    private Pattern(InsnPredicate... checks) {
        this.checks = checks;
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
        InsnPredicate ignore = insn -> false;

        if ((flags & SearchFlags.IGNORE_LABELS) != 0) {
            ignore = ignore.or(type(LABEL));
        }
        if ((flags & SearchFlags.IGNORE_FRAMES) != 0) {
            ignore = ignore.or(type(FRAME));
        }
        if ((flags & SearchFlags.IGNORE_LINES) != 0) {
            ignore = ignore.or(type(LINE));
        }

        return this.find(list, ignore);
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
    public final List<InsnSlice> find(InsnList list, InsnPredicate ignoreInsn) {
        var matches = new ArrayList<InsnSlice>();

        outer:
        for (var i = 0; i <= list.size() - this.checks.length; i++) {
            var last = i; // The last matched instruction

            var offset = 0; // The current instruction offset from the base index
            var matched = 0; // The number of matched instructions
            while (matched < this.checks.length) {
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
                if (!this.checks[matched].test(insn)) {
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
     * @return A stream of this pattern's opcodes
     */
    public final Stream<InsnPredicate> stream() {
        return Arrays.stream(this.checks);
    }

    public static Pattern of(Integer... opcodes) {
        return new Pattern(toChecks(opcodes));
    }

    public static Pattern of(InsnPredicate... checks) {
        return new Pattern(checks);
    }

    public static InsnPredicate[] toChecks(Integer... opcodes) {
        return Arrays.stream(opcodes)
            .map(opcode -> opcode == null ? any() : literal(opcode))
            .toArray(InsnPredicate[]::new);
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
