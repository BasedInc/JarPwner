package me.zero.jarpwner.asm.search.predicate;

import me.zero.jarpwner.asm.search.InsnPredicate;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Brady
 * @since 3/24/2020
 */
public final class LiteralPredicate implements InsnPredicate {

    private final int opcode;

    public LiteralPredicate(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public boolean test(AbstractInsnNode value) {
        return this.opcode == value.getOpcode();
    }

    public final int getOpcode() {
        return this.opcode;
    }
}
