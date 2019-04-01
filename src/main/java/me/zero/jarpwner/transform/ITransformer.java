package me.zero.jarpwner.transform;

import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

/**
 * @author Brady
 * @since 3/31/2019
 */
public interface ITransformer {

    void apply(ClassNode cn);

    boolean accepts(String path);

    Collection<String> getInfo();
}
