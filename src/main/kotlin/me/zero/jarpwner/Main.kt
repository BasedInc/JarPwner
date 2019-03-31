package me.zero.jarpwner

import me.zero.jarpwner.plugin.AllatoriExpiryTransformer
import java.io.File

fun main(/*args: Array<String>*/) {
    println("Reading Jar File")
    val input = JarFileHelper()
    input.read(File("input.jar"))

    val transformers = arrayOf(
        AllatoriExpiryTransformer()
    )

    println("Running Transformers")
    transformers.forEach { transformer ->
        input.getClasses().forEach {
            if (transformer.accepts(it.key)) {
                transformer.apply(it.value)
            }
        }
        transformer.getInfo().forEach {
            println(" [${transformer.javaClass.simpleName}] $it")
        }
    }

    println("Writing Jar File")
    input.write(File("output.jar"))
}
