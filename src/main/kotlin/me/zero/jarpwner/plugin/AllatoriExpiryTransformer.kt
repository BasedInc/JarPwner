package me.zero.jarpwner.plugin

import me.zero.jarpwner.transform.Transformer
import me.zero.jarpwner.extension.completeDesc
import me.zero.jarpwner.extension.findPattern
import me.zero.jarpwner.extension.to
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import java.util.*

class AllatoriExpiryTransformer : Transformer {

    companion object {
        private val PATTERN: Array<Int?> = arrayOf(
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

        private val PATTERN_NO_OBF: Array<Int?> = arrayOf(
            NEW,
            DUP,
            LDC,
            INVOKESPECIAL,
            NEW,
            -1,
            DUP,
            INVOKESPECIAL,
            SWAP,
            INVOKEVIRTUAL,
            IFEQ,
            NEW,
            DUP,
            LDC,
            INVOKESPECIAL,
            ATHROW
        )
    }

    private var removedMatches = 0

    override fun apply(cn: ClassNode) {
        cn.methods.forEach { mn ->
            arrayOf(PATTERN, PATTERN_NO_OBF).forEach { pattern ->
                mn.instructions.findPattern(*pattern).forEach { range ->
                    val from  = range.first
                    val to    = range.second
                    val nodes = from.to(to, inclusive = true)

                    val dateExpiry  = nodes[0]  as TypeInsnNode
                    val dateCurrent = nodes[4]  as TypeInsnNode
                    val dateAfter   = nodes[9]  as MethodInsnNode
                    val throwable   = nodes[11] as TypeInsnNode

                    if (dateExpiry.desc          != "java/util/Date" ||
                        dateCurrent.desc         != "java/util/Date" ||
                        dateAfter.completeDesc() != "java/util/Date.after(Ljava/util/Date;)Z" ||
                        throwable.desc           != "java/lang/Throwable")
                        return

                    nodes.forEach(mn.instructions::remove)

                    removedMatches++
                }
            }
        }
    }

    override fun accepts(path: String): Boolean {
        return true
    }

    override fun getInfo() : Collection<String> {
        return Collections.singletonList("Removed ${this.removedMatches} expiry checks")
    }
}