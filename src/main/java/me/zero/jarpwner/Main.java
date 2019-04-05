package me.zero.jarpwner;

import me.zero.jarpwner.asm.provider.IAcquiredProvider;
import me.zero.jarpwner.asm.provider.jar.IJarFileProvider;
import me.zero.jarpwner.plugin.PluginDiscovery;
import me.zero.jarpwner.transform.ITransformer;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Brady
 * @since 3/31/2019
 */
public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Reading Jar File");
        var input = new JarFileHelper();
        input.read(new File("input.jar"));

        var allatori = PluginDiscovery.getPlugin("allatori");
        if (allatori.isEmpty()) {
            System.out.println("Allatori Plugin not found! Exiting.");
            System.exit(0);
        }

        var allatoriPlugin = allatori.get();
        var transformers = new ArrayList<ITransformer>();

        // TODO: Integrate this with JarFileHelper or an improved jar loading system, this is temporary!

        var classProvider = new IAcquiredProvider<ClassNode>() {
            @Override
            public Map<String, ClassNode> getAll() {
                return input.getClasses();
            }
        };

        var jarFileProvider = new IJarFileProvider() {
            @Override
            public IAcquiredProvider<ClassNode> getClasses() {
                return classProvider;
            }

            @Override
            public IAcquiredProvider<byte[]> getResources() {
                return null;
            }
        };

        allatoriPlugin.getTransformers().forEach(provider -> transformers.add(provider.provide(() -> jarFileProvider)));

        System.out.println("Running Transformers");
        transformers.forEach(ITransformer::setup);
        transformers.forEach(transformer -> {
            input.getClasses().forEach((name, cn) -> {
                if (transformer.accepts(name)) {
                    transformer.apply(cn);
                }
            });

            transformer.getInfo().forEach(info ->
                    System.out.printf(" [%s] %s\n", transformer.getClass().getSimpleName(), info));
        });
        transformers.forEach(ITransformer::cleanup);

        System.out.println("Writing Jar File");
        input.write(new File("output.jar"));
    }
}
