package me.zero.jarpwner.asm.cl;

import org.objectweb.asm.tree.ClassNode;

/**
 * @author Brady
 * @since 4/4/2019
 */
public interface IASMClassLoader {

    Class<?> defineClass(ClassNode node);
}
