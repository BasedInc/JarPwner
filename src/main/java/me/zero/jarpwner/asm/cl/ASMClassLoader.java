package me.zero.jarpwner.asm.cl;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * @author Brady
 * @since 4/4/2019
 */
public class ASMClassLoader extends ClassLoader implements IASMClassLoader {

    private static int num;

    public ASMClassLoader() {
        num++;
    }

    @Override
    public Class<?> defineClass(ClassNode node) {
        var writer = new ClassWriter(COMPUTE_MAXS);
        node.accept(writer);
        byte[] b = writer.toByteArray();
        return defineClass0(node.name.replace('/', '.'), b, 0, b.length);
    }

    @Override
    public String getName() {
        return "ASMClassLoader" + num;
    }

    private Class<?> defineClass0(String name, byte[] b, int off, int len) {
        try {
            return super.defineClass(name, b, off, len);
        } catch (Exception e) {
            return null;
        }
    }
}
