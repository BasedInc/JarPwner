package me.zero.jarpwner.util.jar;

import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * @author Brady
 * @since 4/6/2019
 */
public final class JarWriter {

    private JarWriter() {}

    public static void write(File file, IJarFileProvider jar, Supplier<ClassWriter> writerSupplier) throws IOException {
        var jos = new JarOutputStream(new FileOutputStream(file));

        for (var entry : jar.getClasses().getAll().entrySet()) {
            var writer = writerSupplier.get();
            entry.getValue().accept(writer);

            var jarEntry = new JarEntry(entry.getKey() + ".class");
            jos.putNextEntry(jarEntry);
            jos.write(writer.toByteArray());
            jos.closeEntry();
        }

        for (var entry : jar.getResources().getAll().entrySet()) {
            var jarEntry = new JarEntry(entry.getKey());
            jos.putNextEntry(jarEntry);
            jos.write(entry.getValue());
            jos.closeEntry();
        }

        jos.close();
    }
}
