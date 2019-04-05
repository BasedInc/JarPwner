package me.zero.jarpwner.asm.provider.jar;

import me.zero.jarpwner.asm.provider.IAcquiredProvider;
import org.objectweb.asm.tree.ClassNode;

/**
 * A composite of a {@link ClassNode} acquired provider and a {@code byte[]} acquired
 * provider which is reflective of a jar file.
 *
 * @author Brady
 * @since 4/2/2019
 */
public interface IJarFileProvider {

    /**
     * @return An {@link IAcquiredProvider} providing all of the acquired classes
     */
    IAcquiredProvider<ClassNode> getClasses();

    /**
     * @return An {@link IAcquiredProvider} providing all of the acquired resources
     */
    IAcquiredProvider<byte[]> getResources();
}
