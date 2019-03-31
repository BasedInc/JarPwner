package me.zero.jarutil.plugin

import me.zero.jarutil.transform.Transformer
import me.zero.jarutil.extension.completeDesc
import me.zero.jarutil.extension.findPattern
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import java.util.*

class AllatoriExpiryTransformer : Transformer {

    private var removedMatches = 0

    override fun apply(cn: ClassNode) {
        cn.methods.forEach { mn ->
            val matches = mn.instructions.findPattern(
                NEW,
                DUP,
                LDC,
                INVOKESPECIAL,
                NEW,
                DUP,
                -1,
                INVOKESPECIAL,
                SWAP,
                INVOKEVIRTUAL,
                IFEQ,
                NEW,
                DUP,
                LDC,
                INVOKESTATIC,
                INVOKESPECIAL,
                ATHROW
            )

            matches.forEach matches@ { insn ->
                var current = insn.first
                val last = insn.second.next // ensure we're including that last instruction

                val toRemove = ArrayList<AbstractInsnNode>()
                while (current != last) {
                    toRemove.add(current)
                    current = current.next
                }

                val dateExpiry  = toRemove[0]  as TypeInsnNode
                val dateCurrent = toRemove[4]  as TypeInsnNode
                val dateAfter   = toRemove[9]  as MethodInsnNode
                val throwable   = toRemove[11] as TypeInsnNode

                if (dateExpiry.desc          != "java/util/Date" ||
                    dateCurrent.desc         != "java/util/Date" ||
                    dateAfter.completeDesc() != "java/util/Date.after(Ljava/util/Date;)Z" ||
                    throwable.desc           != "java/lang/Throwable")
                    return@matches

                toRemove.forEach(mn.instructions::remove)

                removedMatches++
            }
        }
    }

    override fun accepts(path: String): Boolean {
        return true
    }

    override fun printInfo() {
        println(" AllatoriExpiryTransformer removed ${this.removedMatches} expiry checks")
    }
}