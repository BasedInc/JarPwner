package me.zero.jarpwner.allatori.transformer;

import me.zero.jarpwner.allatori.util.Patterns;
import me.zero.jarpwner.transform.ITransformer;
import me.zero.jarpwner.transform.TransformerMeta;
import me.zero.jarpwner.transform.exception.TransformerException;
import me.zero.jarpwner.asm.Pattern;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Brady
 * @since 3/31/2019
 */
@TransformerMeta(
        name = "Watermark Remover",
        desc = "Removes watermarks that are used to trace the source jar back to the client who it belongs to"
)
public class AllatoriWatermarkTransformer implements ITransformer {

    /**
     * Counter to keep track of the amount of watermarks removed by this transformer instance
     */
    private int removedMatches;

    @Override
    public void apply(ClassNode cn) {
        cn.methods.forEach(mn -> Patterns.WATERMARK_PATTERN.find(mn.instructions, Pattern.SearchFlags.IGNORE_ALL).forEach(slice -> {
            if (slice.delete(mn.instructions)) {
                removedMatches++;
            } else {
                throw new TransformerException("Pattern matched but slice instruction list was unable to be constructed");
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
