package me.zero.jarpwner;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * @author Brady
 * @since 3/31/2019
 */
public class JarFileHelper {

    private final Map<String, ClassNode> classes = new HashMap<>();
    private final Map<String, byte[]> resources = new HashMap<>();

    public final void read(File file) throws IOException {
        classes.clear();
        resources.clear();

        var jarFile = new JarFile(file);

        Enumeration<JarEntry> enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry entry = enumeration.nextElement();
            var inputStream = jarFile.getInputStream(entry);
            var bytes = IOUtils.toByteArray(inputStream);
            var name = entry.getName();

            if (name.endsWith(".class")) {
                var reader = new ClassReader(bytes);
                var node = new ClassNode();
                reader.accept(node, 0);
                classes.put(name, node);
            } else {
                resources.put(name, bytes);
            }
        }
    }

    public final void write(File file) throws IOException {
        var jos = new JarOutputStream(new FileOutputStream(file));

        for (Map.Entry<String, ClassNode> entry : classes.entrySet()) {
            var writer = new ClassWriter(COMPUTE_MAXS);
            entry.getValue().accept(writer);

            var jarEntry = new JarEntry(entry.getKey());
            jos.putNextEntry(jarEntry);
            jos.write(writer.toByteArray());
            jos.closeEntry();
        }

        for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
            var jarEntry = new JarEntry(entry.getKey());
            jos.putNextEntry(jarEntry);
            jos.write(entry.getValue());
            jos.closeEntry();
        }

        jos.close();
    }

    public final Map<String, ClassNode> getClasses() {
        return this.classes;
    }

    public final Map<String, byte[]> getResources() {
        return this.resources;
    }
}
