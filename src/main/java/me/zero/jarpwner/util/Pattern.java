package me.zero.jarpwner.util;

import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brady
 * @since 3/31/2019
 */
public class Pattern {

    private final Integer[] opcodes;

    private Pattern(Integer... opcodes) {
        this.opcodes = opcodes;
    }

    public List<InsnRange> find(InsnList list) {
        List<InsnRange> matches = new ArrayList<>();

        outer:
        for (int i = 0; i <= list.size() - this.opcodes.length; i++) {
            var last = list.get(i);

            for (int j = 0; j < this.opcodes.length; j++) {
                var index = i + j;

                if (index >= list.size()) {
                    continue outer;
                }

                var insn = list.get(index);

                if (insn.getOpcode() != opcodes[j] && opcodes[j] != null) {
                    continue outer;
                }

                last = insn;
            }

            matches.add(InsnRange.of(list.get(i), last));
        }

        return matches;
    }

    public static Pattern of(Integer... opcodes) {
        return new Pattern(opcodes);
    }
}
