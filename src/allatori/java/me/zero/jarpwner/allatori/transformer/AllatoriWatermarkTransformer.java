package me.zero.jarpwner.allatori.transformer;

import me.zero.jarpwner.transform.ITransformer;
import me.zero.jarpwner.transform.TransformerMeta;
import me.zero.jarpwner.util.Pattern;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;
import java.util.Collections;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Brady
 * @since 3/31/2019
 */
@TransformerMeta(
        name = "Watermark Remover",
        desc = "Removes watermarks that are used to trace the source jar back to the client who it belongs to"
)
public class AllatoriWatermarkTransformer implements ITransformer {

    private static final Pattern PATTERN = Pattern.of(
            SIPUSH,
            SIPUSH,
            SIPUSH,
            SIPUSH,
            POP2,
            POP2
    );

    private int removedMatches;

    @Override
    public void apply(ClassNode cn) {
        cn.methods.forEach(mn -> PATTERN.find(mn.instructions, Pattern.SearchFlags.IGNORE_ALL).forEach(range -> {
            if (range.delete(mn.instructions)) {
                removedMatches++;
            }
        }));
    }

    @Override
    public boolean accepts(String path) {
        return true;
    }

    @Override
    public Collection<String> getInfo() {
        return Collections.singletonList(
                String.format("Removed %d watermark chunk(s)", this.removedMatches)
        );
    }
}
