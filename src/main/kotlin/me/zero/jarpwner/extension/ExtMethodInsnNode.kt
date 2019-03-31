package me.zero.jarpwner.extension

import org.objectweb.asm.tree.MethodInsnNode

fun MethodInsnNode.completeDesc() : String {
    return this.owner + "." + this.name + this.desc
}
