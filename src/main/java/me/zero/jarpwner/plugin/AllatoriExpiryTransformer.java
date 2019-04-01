package me.zero.jarpwner.plugin;

import me.zero.jarpwner.transform.Transformer;
import me.zero.jarpwner.util.Pattern;
import me.zero.jarpwner.util.Util;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Brady
 * @since 3/31/2019
 */
public class AllatoriExpiryTransformer implements Transformer {

    private static final Pattern PATTERN = Pattern.of(
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

    private static final Pattern PATTERN_NO_OBF = Pattern.of(
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
            INVOKESPECIAL,
            ATHROW
    );

    private static final List<Pattern> PATTERNS = Arrays.asList(PATTERN, PATTERN_NO_OBF);

    private int removedMatches;

    @Override
    public final void apply(ClassNode cn) {
        cn.methods.forEach(mn -> PATTERNS.forEach(pattern -> pattern.find(mn.instructions, Pattern.SearchFlags.IGNORE_ALL).forEach(range -> {
            var insns = range.getAll(true);
            if (insns == null) {
                return;
            }

            var newInsns = insns.<TypeInsnNode>allWithOpcode(NEW);

            if (!"java/util/Date".equals(newInsns.get(0).desc))
                return;

            if (!"java/util/Date".equals(newInsns.get(1).desc))
                return;

            if (!"java/lang/Throwable".equals(newInsns.get(2).desc))
                return;

            if (!"java/util/Date.after(Ljava/util/Date;)Z".equals(Util.getFullDesc(insns.firstWithOpcode(INVOKEVIRTUAL))))
                return;

            range.delete(mn.instructions);

            removedMatches++;
        })));
    }

    @Override
    public final boolean accepts(String path) {
        return true;
    }

    @Override
    public final Collection<String> getInfo() {
        return Collections.singletonList(
                String.format("Removed %d expiry check(s)", this.removedMatches)
        );
    }
}
