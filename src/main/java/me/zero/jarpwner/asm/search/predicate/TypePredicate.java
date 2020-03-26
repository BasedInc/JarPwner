package me.zero.jarpwner.asm.search.predicate;

import me.zero.jarpwner.asm.search.InsnPredicate;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Brady
 * @since 3/25/2020
 */
public final class TypePredicate implements InsnPredicate {

    private final int type;

    public TypePredicate(int type) {
        this.type = type;
    }

    @Override
    public boolean test(AbstractInsnNode value) {
        return this.type == value.getType();
    }

    public final int getType() {
        return this.type;
    }
}
