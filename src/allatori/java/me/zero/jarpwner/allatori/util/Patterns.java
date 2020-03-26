package me.zero.jarpwner.allatori.util;

import me.zero.jarpwner.asm.search.InsnPredicate;
import me.zero.jarpwner.asm.search.Pattern;
import me.zero.jarpwner.asm.search.predicate.LiteralPredicate;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Brady
 * @since 4/1/2019
 */
public interface Patterns {

    Pattern STRING_DECRYPT = Pattern.of(
            LDC,
            INVOKESTATIC
    );

    Pattern EXPIRY_PATTERN = Pattern.of(
            NEW,
            DUP,
            LDC,
            INVOKESPECIAL,
            NEW,
            DUP,
            INVOKESPECIAL,
            SWAP,
            INVOKEVIRTUAL,
            IFEQ,
            NEW,
            DUP,
            LDC,
            INVOKESTATIC,
            INVOKESPECIAL,
            ATHROW
    );

    Pattern EXPIRY_PATTERN_NO_OBF = Pattern.of(
            EXPIRY_PATTERN.stream()
                .filter(check -> !(check instanceof LiteralPredicate) || ((LiteralPredicate) check).getOpcode() != INVOKESTATIC)
                .toArray(InsnPredicate[]::new)
    );

    Pattern WATERMARK_PATTERN = Pattern.of(
            SIPUSH,
            SIPUSH,
            SIPUSH,
            SIPUSH,
            POP2,
            POP2
    );
}
