package me.zero.jarpwner.plugin;

import me.zero.jarpwner.transform.Transformer;
import me.zero.jarpwner.util.Pattern;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;
import java.util.Collections;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Brady
 * @since 3/31/2019
 */
public class AllatoriWatermarkTransformer implements Transformer {

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
            var insns = range.getAll(true);
            if (insns == null) {
                return;
            }
            insns.forEach(mn.instructions::remove);
            removedMatches++;
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
