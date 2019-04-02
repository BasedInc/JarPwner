package me.zero.jarpwner.allatori.transformer;

import me.zero.jarpwner.allatori.util.Patterns;
import me.zero.jarpwner.transform.ITransformer;
import me.zero.jarpwner.transform.TransformerMeta;
import me.zero.jarpwner.transform.exception.TransformerException;
import me.zero.jarpwner.asm.Pattern;
import me.zero.jarpwner.asm.Util;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Brady
 * @since 3/31/2019
 */
@TransformerMeta(
        name = "Expiry Remover",
        desc = "Removes expiry checks that are used to prevent the program from operating after a certain date"
)
public class AllatoriExpiryTransformer implements ITransformer {

    private static final List<Pattern> PATTERNS = Arrays.asList(
            Patterns.EXPIRY_PATTERN,
            Patterns.EXPIRY_PATTERN_NO_OBF
    );

    /**
     * Counter to keep track of the amount of expiry checks removed by this transformer instance
     */
    private int removedMatches;

    @Override
    public final void apply(ClassNode cn) {
        cn.methods.forEach(mn -> PATTERNS.forEach(pattern -> pattern.find(mn.instructions, Pattern.SearchFlags.IGNORE_ALL).forEach(slice -> {
            var insns = slice.getAll(true);
            if (insns == null) {
                throw new TransformerException("Pattern matched but slice instruction list was unable to be constructed");
            }

            var newInsns = insns.<TypeInsnNode>allWithOpcode(NEW);
            if (newInsns.size() != 3) {
                throw new TransformerException("Pattern matched but 3 NEW instructions were not found!");
            }

            if (!"java/util/Date".equals(newInsns.get(0).desc))
                return;

            if (!"java/util/Date".equals(newInsns.get(1).desc))
                return;

            if (!"java/lang/Throwable".equals(newInsns.get(2).desc))
                return;

            if (!"java/util/Date.after(Ljava/util/Date;)Z".equals(Util.getFullDesc(insns.firstWithOpcode(INVOKEVIRTUAL))))
                return;

            slice.delete(mn.instructions);

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
