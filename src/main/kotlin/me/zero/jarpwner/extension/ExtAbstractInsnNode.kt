package me.zero.jarpwner.extension

import org.objectweb.asm.tree.AbstractInsnNode
import java.util.ArrayList

fun AbstractInsnNode.to(node: AbstractInsnNode, inclusive: Boolean = false) : List<AbstractInsnNode> {
    var current = this
    var last = node

    if (inclusive) {
        last = last.next
    }

    val nodes = ArrayList<AbstractInsnNode>()
    while (current != last) {
        nodes.add(current)
        current = current.next
    }

    return nodes
}
