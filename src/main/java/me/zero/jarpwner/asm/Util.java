package me.zero.jarpwner.asm;

import org.objectweb.asm.tree.MethodInsnNode;

/**
 * @author Brady
 * @since 3/31/2019
 */
public final class Util {

    private Util() {}

    public static String getFullDesc(MethodInsnNode insn) {
        return insn == null ? null : insn.owner + "." + insn.name + insn.desc;
    }
}
