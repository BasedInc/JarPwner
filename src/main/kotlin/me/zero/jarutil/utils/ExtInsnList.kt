package me.zero.jarutil.utils

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.AbstractInsnNode.*
import org.objectweb.asm.tree.InsnList

fun InsnList.findPattern(vararg opcodes: Int?) : List<Pair<AbstractInsnNode, AbstractInsnNode>> {
    val insns = this.toArray()

    val matches = ArrayList<Pair<AbstractInsnNode, AbstractInsnNode>>()

    outer@
    for (i in 0..insns.size - opcodes.size) {
        var found = true
        var last : AbstractInsnNode = this[i]

        for (j in 0 until opcodes.size) {
            val index = i + j

            if (index > insns.lastIndex) {
                continue@outer
            }

            val insn = this[index]

            if (insn.opcode != opcodes[j] && opcodes[j] != null) {
                found = false
            }

            last = insn
        }

        if (found) {
            matches.add(Pair(this[i], last))
        }
    }
    return matches
}
