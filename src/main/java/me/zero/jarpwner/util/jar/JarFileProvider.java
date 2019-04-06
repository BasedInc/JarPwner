package me.zero.jarpwner.util.jar;

import me.zero.jarpwner.util.provider.IAcquiredProvider;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Brady
 * @since 4/6/2019
 */
class JarFileProvider implements IJarFileProvider {

    private IAcquiredProvider<ClassNode> classes;
    private IAcquiredProvider<byte[]> resources;

    JarFileProvider(IAcquiredProvider<ClassNode> classes, IAcquiredProvider<byte[]> resources) {
        this.classes = classes;
        this.resources = resources;
    }

    @Override
    public IAcquiredProvider<ClassNode> getClasses() {
        return this.classes;
    }

    @Override
    public IAcquiredProvider<byte[]> getResources() {
        return this.resources;
    }
}
