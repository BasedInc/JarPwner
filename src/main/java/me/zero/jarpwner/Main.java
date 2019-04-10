package me.zero.jarpwner;

import me.zero.jarpwner.plugin.IPlugin;
import me.zero.jarpwner.util.jar.IJarFileProvider;
import me.zero.jarpwner.util.jar.JarReader;
import me.zero.jarpwner.util.jar.JarWriter;
import me.zero.jarpwner.plugin.PluginDiscovery;
import me.zero.jarpwner.transform.ITransformer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Brady
 * @since 3/31/2019
 */
public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Reading Jar File");
        var jarFileProvider = JarReader.read(new File("input.jar"));

        var allatori = PluginDiscovery.getPlugin("allatori");
        if (allatori.isEmpty()) {
            System.out.println("Allatori Plugin not found! Exiting.");
            System.exit(0);
        }

        var pwner = PluginDiscovery.getPlugin("pwner");
        if (pwner.isEmpty()) {
            System.out.println("Pwner Plugin not found! Exiting.");
            System.exit(0);
        }

        var transformers = new ArrayList<ITransformer>();
        transformers.addAll(getTransformers(allatori.get(), jarFileProvider));
        transformers.addAll(getTransformers(pwner.get(), jarFileProvider));

        System.out.println("Running Transformers");
        transformers.forEach(ITransformer::setup);
        transformers.forEach(transformer -> {
            jarFileProvider.getClasses().getAll().forEach((name, cn) -> {
                if (transformer.accepts(name)) {
                    transformer.apply(cn);
                }
            });

            transformer.getInfo().forEach(info ->
                    System.out.printf(" [%s] %s\n", transformer.getClass().getSimpleName(), info));
        });
        transformers.forEach(ITransformer::cleanup);

        System.out.println("Writing Jar File");
        JarWriter.write(new File("output.jar"), jarFileProvider);
    }

    private static List<ITransformer> getTransformers(IPlugin plugin, IJarFileProvider provider) {
        return plugin.getTransformers().stream().map(p -> p.provide(() -> provider)).collect(Collectors.toList());
    }
}
