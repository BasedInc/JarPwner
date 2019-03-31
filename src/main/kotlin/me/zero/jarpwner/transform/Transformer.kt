package me.zero.jarpwner.transform

import org.objectweb.asm.tree.ClassNode

/**
 * @author Brady
 * @since 3/30/2019
 */
interface Transformer {

    fun apply(cn: ClassNode)

    fun accepts(path: String): Boolean

    fun getInfo() : Collection<String>
}
