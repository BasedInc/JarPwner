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

    public static InsnPredicate type(int... types) {
        switch (types.length) {
            case 0: {
                throw new IllegalArgumentException("At least one type must be provided");
            }
            case 1: {
                return new TypePredicate(types[0]);
            }
            default: {
                return Arrays.stream(types).boxed().reduce(
                    type -> false,
                    (check, type) -> check.or(type(type)),
                    InsnPredicate::or
                );
            }
        }
    }

    public static InsnPredicate literal(int... opcodes) {
        switch (opcodes.length) {
            case 0: {
                throw new IllegalArgumentException("At least one opcode must be provided");
            }
            case 1: {
                return new LiteralPredicate(opcodes[0]);
            }
            default: {
                return Arrays.stream(opcodes).boxed().reduce(
                    opcode -> false,
                    (check, opcode) -> check.or(literal(opcode)),
                    InsnPredicate::or
                );
            }
        }
    }

    public static InsnPredicate any() {
        return i -> true;
    }
}
