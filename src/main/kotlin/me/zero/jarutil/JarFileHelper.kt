package me.zero.jarutil

import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_FRAMES
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class JarFileHelper {

    private val classes: MutableMap<String, ClassNode> = HashMap()
    private val resources: MutableMap<String, ByteArray> = HashMap()

    @Throws(IOException::class)
    fun read(file: File) {
        classes.clear()
        resources.clear()

        val jarFile = JarFile(file)

        jarFile.stream().forEach { entry ->
            val inputStream = jarFile.getInputStream(entry)
            val bytes = IOUtils.toByteArray(inputStream)
            val name = entry.name

            if (name.endsWith(".class")) {
                val reader = ClassReader(bytes)
                val node = ClassNode()
                reader.accept(node, 0)
                classes[name] = node
            } else {
                resources[name] = bytes
            }
        }
    }

    @Throws(IOException::class)
    fun write(file: File) {
        val jos = JarOutputStream(FileOutputStream(file))

        classes.forEach {
            var writer = ClassWriter(COMPUTE_FRAMES or COMPUTE_MAXS)
            try {
                it.value.accept(writer)
            } catch (e: Exception) {
                writer = ClassWriter(COMPUTE_MAXS)
                it.value.accept(writer)
            }

            val entry = JarEntry(it.key)
            jos.putNextEntry(entry)
            jos.write(writer.toByteArray())
            jos.closeEntry()
        }

        resources.forEach {
            val entry = JarEntry(it.key)
            jos.putNextEntry(entry)
            jos.write(it.value)
            jos.closeEntry()
        }

        jos.close()
    }

    fun getClasses() : Map<String, ClassNode> {
        return this.classes
    }

    fun getResources() : Map<String, ByteArray> {
        return this.resources
    }
}