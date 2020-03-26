package me.zero.jarpwner.asm.search;

import me.zero.jarpwner.asm.search.predicate.LiteralPredicate;
import me.zero.jarpwner.asm.search.predicate.TypePredicate;

import java.util.Arrays;

/**
 * @author Brady
 * @since 3/24/2020
 */
public final class PatternUtils {

    private PatternUtils() {}

    public static InsnPredicate literal(int opcode) {
        return new LiteralPredicate(opcode);
    }

    public static InsnPredicate type(int type) {
        return new TypePredicate(type);
    }

    public static InsnPredicate anyLiteral(int... opcodes) {
        return Arrays.stream(opcodes).boxed().reduce(
            opcode -> false,
            (check, opcode) -> check.or(literal(opcode)),
            InsnPredicate::or
        );
    }

    public static InsnPredicate anyType(int... types) {
        return Arrays.stream(types).boxed().reduce(
            opcode -> false,
            (check, type) -> check.or(type(type)),
            InsnPredicate::or
        );
    }

    public static InsnPredicate any() {
        return i -> true;
    }
}
