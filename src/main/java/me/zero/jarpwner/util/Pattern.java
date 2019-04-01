package me.zero.jarpwner.util;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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

    public final List<InsnRange> find(InsnList list, int flags) {
        return this.find(list, insn -> {
            switch (insn.getType()) {
                case LABEL: {
                    if ((flags & SearchFlags.IGNORE_LABELS) != 0) return true;
                }
                case FRAME: {
                    if ((flags & SearchFlags.IGNORE_FRAMES) != 0) return true;
                }
                case LINE: {
                    if ((flags & SearchFlags.IGNORE_LINES) != 0) return true;
                }
            }
            return false;
        });
    }

    public final List<InsnRange> find(InsnList list, Predicate<AbstractInsnNode> ignoreInsn) {
        List<InsnRange> matches = new ArrayList<>();

        outer:
        for (int i = 0; i <= list.size() - this.opcodes.length; i++) {
            var last = list.get(i);

            int offset = 0;
            int matched = 0;
            while (matched < this.opcodes.length) {
                var index = i + offset++;

                if (index >= list.size()) {
                    continue outer;
                }

                var insn = list.get(index);

                // First instruction MUST be what we're looking for, only apply the ignore check once we're past it
                if (matched > 0 && ignoreInsn.test(insn))
                    continue ;

                if (opcodes[matched] != null && insn.getOpcode() != opcodes[matched]) {
                    continue outer;
                }

                last = insn;
                matched++;
            }

            matches.add(InsnRange.of(list.get(i), last));
        }

        return matches;
    }

    public final Integer[] getOpcodes() {
        return this.opcodes.clone();
    }

    public static Pattern of(Integer... opcodes) {
        return new Pattern(opcodes);
    }

    public static Pattern of(List<Integer> opcodes) {
        return new Pattern(opcodes.toArray(new Integer[0]));
    }

    public interface SearchFlags {

        @SuppressWarnings("PointlessBitwiseExpression")
        int IGNORE_LABELS = 1 << 0;
        int IGNORE_FRAMES = 1 << 1;
        int IGNORE_LINES  = 1 << 2;

        int IGNORE_ALL = IGNORE_LABELS | IGNORE_FRAMES | IGNORE_LINES;
    }
}
