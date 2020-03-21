package me.zero.jarpwner.util.jar;

import me.zero.jarpwner.asm.AsmUtils;
import me.zero.jarpwner.util.provider.IAcquiredProvider;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.jar.JarFile;

/**
 * @author Brady
 * @since 4/6/2019
 */
public class JarReader {

    private JarReader() {}

    public static IJarFileProvider read(File file) throws IOException {
        var jarFile = new JarFile(file);

        var classes = new HashMap<String, ClassNode>();
        var resources = new HashMap<String, byte[]>();

        var enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            var entry       = enumeration.nextElement();
            var inputStream = jarFile.getInputStream(entry);
            var bytes       = IOUtils.toByteArray(inputStream);
            var name        = entry.getName();

            if (name.endsWith(".class")) {
                classes.put(name.substring(0, name.length() - ".class".length()), AsmUtils.classFromBytes(bytes, 0));
            } else {
                resources.put(name, bytes);
            }
        }

        return new JarFileProvider(IAcquiredProvider.byMap(classes), IAcquiredProvider.byMap(resources));
    }
}
