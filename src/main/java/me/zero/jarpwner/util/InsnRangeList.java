package me.zero.jarpwner.util;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Brady
 * @since 3/31/2019
 */
public final class InsnRangeList extends ArrayList<AbstractInsnNode> {

    @SuppressWarnings("unchecked")
    public <T extends AbstractInsnNode> List<T> allWithOpcode(int opcode) {
        return this.stream()
                .filter(insn -> insn.getOpcode() == opcode)
                .map(insn -> (T) insn)
                .collect(Collectors.toList());
    }

    public <T extends AbstractInsnNode> T firstWithOpcode(int opcode) {
        List<T> opcodes = allWithOpcode(opcode);
        return opcodes.size() > 0 ? opcodes.get(0) : null;
    }
}
