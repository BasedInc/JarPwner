package me.zero.jarpwner.asm;

import me.zero.jarpwner.util.provider.IProvider;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Brady
 * @since 3/31/2019
 */
public final class AsmUtils {

    private AsmUtils() {}

    private static final Map<Integer, String> OPCODE_TO_NAME = new HashMap<>();

    static {
        for (Field f : Opcodes.class.getFields()) {
            if (f.getType() != int.class)
                continue;

            String name = f.getName();
            if (name.startsWith("ASM")
                    || name.startsWith("SOURCE")
                    || name.startsWith("V")
                    || name.startsWith("ACC_")
                    || name.startsWith("T_")
                    || name.startsWith("H_")
                    || name.startsWith("F_"))
                continue;

            try {
                OPCODE_TO_NAME.put((int) f.get(null), name);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFullDesc(MethodInsnNode insn) {
        return insn == null ? null : insn.owner + "." + insn.name + insn.desc;
    }

    public static ClassNode classFromBytes(byte[] bytes, int parsingOptions) {
        var reader = new ClassReader(bytes);
        var node = new ClassNode();
        reader.accept(node, parsingOptions);
        return node;
    }

    public static boolean isAssignableFrom(IProvider<ClassNode> provider, ClassNode from, ClassNode cn) {
        if (from.name.equals(cn.name)) {
            return true;
        }

        for (var iface : cn.interfaces) {
            ClassNode interfaceNode = provider.forName(iface);
            if (interfaceNode != null && isAssignableFrom(provider, from, interfaceNode)) {
                return true;
            }
        }

        if (cn.superName != null) {
            ClassNode superNode = provider.forName(cn.superName);
            return superNode != null && isAssignableFrom(provider, from, superNode);
        }

        return false;
    }

    public static void forEachInsn(InsnList list, Consumer<AbstractInsnNode> consumer) {
        iterable(list).forEach(consumer);
    }

    public static Stream<AbstractInsnNode> stream(InsnList list) {
        return StreamSupport.stream(iterable(list).spliterator(), false);
    }

    public static Iterable<AbstractInsnNode> iterable(InsnList list) {
        var iterator = list.iterator();
        return () -> iterator;
    }

    public static String getOpcodeName(int opcode) {
        return OPCODE_TO_NAME.get(opcode);
    }
}
