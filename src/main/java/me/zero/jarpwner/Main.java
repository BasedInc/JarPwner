package me.zero.jarpwner;

import me.zero.jarpwner.plugin.PluginDiscovery;
import me.zero.jarpwner.transform.ITransformer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

        allatoriPlugin.getTransformers().forEach(provider -> transformers.add(provider.provide()));

        System.out.println("Running Transformers");
        transformers.forEach(transformer -> {
            input.getClasses().forEach((name, cn) -> {
                if (transformer.accepts(name)) {
                    transformer.apply(cn);
                }
            });

            transformer.getInfo().forEach(info ->
                    System.out.printf(" [%s] %s\n", transformer.getClass().getSimpleName(), info));
        });

        System.out.println("Writing Jar File");
        input.write(new File("output.jar"));
    }
}
