package me.zero.jarpwner.asm.search;

import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Brady
 * @since 3/25/2020
 */
@FunctionalInterface
public interface InsnPredicate {

    boolean test(AbstractInsnNode t);

    default InsnPredicate and(InsnPredicate other) {
        return t -> this.test(t) && other.test(t);
    }

    default InsnPredicate negate() {
        return t -> !this.test(t);
    }

    default InsnPredicate or(InsnPredicate other) {
        return t -> this.test(t) || other.test(t);
    }
}
