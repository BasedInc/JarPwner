package me.zero.jarpwner.asm;

import me.zero.jarpwner.util.provider.IProvider;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;

/**
 * @author Brady
 * @since 3/31/2019
 */
public final class ContextClassWriter extends ClassWriter {

    private static final IProvider<ClassNode> SYSTEM_CLASS_PROVIDER = IProvider.byClassLoader(ClassLoader.getSystemClassLoader());

    private IProvider<ClassNode> provider;

    public ContextClassWriter(int flags, IProvider<ClassNode> provider) {
        super(flags);
        // TODO: Better handling of this
        // - the actual parent IJarFileProvider should be a composite of all input jars (whats actually modified)
        // - fallbacks should only be libraries and jre
        this.provider = provider.withFallback(SYSTEM_CLASS_PROVIDER);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        ClassNode class1 = this.provider.forName(type1);
        ClassNode class2 = this.provider.forName(type2);

        if (class1 == null || class2 == null) {
            System.out.println(type1 + " " + SYSTEM_CLASS_PROVIDER.forName(type1));
            System.out.println(type2 + " " + SYSTEM_CLASS_PROVIDER.forName(type2));
            return super.getCommonSuperClass(type1, type2);
        } else if (AsmUtils.isAssignableFrom(this.provider, class1, class2)) {
            return type1;
        } else if (AsmUtils.isAssignableFrom(this.provider, class2, class1)) {
            return type2;
        } else if (Modifier.isInterface(class1.access) || Modifier.isInterface(class2.access)) {
            return "java/lang/Object";
        } else {
            do {
                class1 = this.provider.forName(class1.superName);
            } while (!AsmUtils.isAssignableFrom(this.provider, class1, class2));
            return type1;
        }
    }
}
