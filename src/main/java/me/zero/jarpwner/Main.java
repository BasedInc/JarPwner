package me.zero.jarpwner;

import me.zero.jarpwner.plugin.IPlugin;
import me.zero.jarpwner.util.jar.IJarFileProvider;
import me.zero.jarpwner.util.jar.JarReader;
import me.zero.jarpwner.util.jar.JarWriter;
import me.zero.jarpwner.plugin.PluginDiscovery;
import me.zero.jarpwner.transform.ITransformer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Brady
 * @since 3/31/2019
 */
public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Reading Jar File");
        var jarFileProvider = JarReader.read(new File("input.jar"));

        var transformers = new ArrayList<ITransformer>();
        showSelection(plugin -> transformers.addAll(getTransformers(plugin, jarFileProvider)));

        if (transformers.size() == 0) {
            System.out.println("No transformers provided, exiting.");
            return;
        }

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

    private static void showSelection(Consumer<IPlugin> pluginCallback) {
        var items = new ArrayList<SelectorItem>();
        boolean[] menu = { true }; // lol

        PluginDiscovery.getPlugins().forEach(plugin ->
            items.add(new SelectorItem(
                plugin.getName(),
                () -> pluginCallback.accept(plugin)))
        );

        items.add(new SelectorItem("(Exit)", () -> menu[0] = false));

        Scanner s = new Scanner(System.in);
        while (menu[0]) {
            System.out.println("Select Available Plugins");
            for (int i = 0; i < items.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, items.get(i).label);
            }
            System.out.print("#: ");

            SelectorItem action = items.get(s.nextInt() - 1);
            action.action.run();
            items.remove(action);

            // If we selected all available transformers don't require the user to exit
            if (items.size() == 1) {
                break;
            }
        }
        s.close();
    }

    private static final class SelectorItem {
        String label;
        Runnable action;

        public SelectorItem(String label, Runnable action) {
            this.label = label;
            this.action = action;
        }
    }
}
