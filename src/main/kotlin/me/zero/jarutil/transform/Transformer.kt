package me.zero.jarutil.transform

import org.objectweb.asm.tree.ClassNode

/**
 * @author Brady
 * @since 3/30/2019
 */
interface Transformer {

    fun apply(cn: ClassNode)

    fun accepts(path: String): Boolean

    fun printInfo()
}
