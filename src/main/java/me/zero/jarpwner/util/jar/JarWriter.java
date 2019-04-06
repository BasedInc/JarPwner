package me.zero.jarpwner.util.jar;

import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * @author Brady
 * @since 4/6/2019
 */
public class JarWriter {

    private JarWriter() {}

    public static void write(File file, IJarFileProvider provider) throws IOException {
        var jos = new JarOutputStream(new FileOutputStream(file));

        for (var entry : provider.getClasses().getAll().entrySet()) {
            var writer = new ClassWriter(COMPUTE_MAXS);
            entry.getValue().accept(writer);

            var jarEntry = new JarEntry(entry.getKey());
            jos.putNextEntry(jarEntry);
            jos.write(writer.toByteArray());
            jos.closeEntry();
        }

        for (var entry : provider.getResources().getAll().entrySet()) {
            var jarEntry = new JarEntry(entry.getKey());
            jos.putNextEntry(jarEntry);
            jos.write(entry.getValue());
            jos.closeEntry();
        }

        jos.close();
    }
}
