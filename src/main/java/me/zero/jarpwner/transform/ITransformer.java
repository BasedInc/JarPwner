package me.zero.jarpwner.transform;

import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

/**
 * @author Brady
 * @since 3/31/2019
 */
public interface ITransformer {

    /**
     * Called before {@link #apply(ClassNode)} is called for all target classes, allowing
     * a period for initialization that doesn't directly apply to transformation to occur.
     */
    default void setup() {}

    /**
     * Called for each {@link ClassNode} that is accepted by {@link #accepts(String)}.
     *
     * @param cn The class node with an accepted path
     */
    void apply(ClassNode cn);

    /**
     * Called after the transform sequence is complete, denoting that no further calls to
     * {@link #apply(ClassNode)} will be made on this instance. Allows any post-transform
     * cleanup to occur that is needed.
     */
    default void cleanup() {}

    /**
     * Called prior to {@link #apply(ClassNode)} to determine if this transformer would like
     * to accept a {@link ClassNode} on the basis of its path.
     *
     * @param path
     * @return Whether or not the path is accepted
     */
    default boolean accepts(String path) {
        return true;
    }

    Collection<String> getInfo();
}
