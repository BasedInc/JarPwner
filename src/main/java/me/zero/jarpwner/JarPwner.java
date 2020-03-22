package me.zero.jarpwner;

import me.zero.jarpwner.asm.ContextClassWriter;
import me.zero.jarpwner.plugin.IPlugin;
import me.zero.jarpwner.plugin.PluginDiscovery;
import me.zero.jarpwner.transform.ITransformer;
import me.zero.jarpwner.util.jar.IJarFileProvider;
import me.zero.jarpwner.util.jar.JarReader;
import me.zero.jarpwner.util.jar.JarWriter;
import me.zero.jarpwner.util.provider.IProvider;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * @author Brady
 * @since 3/21/2020
 */
public enum JarPwner {
    INSTANCE;

    public void run(Options options) throws IOException {
        System.out.printf("Reading Input %s\n", options.input.toAbsolutePath());
        var jarFileProvider = JarReader.read(options.input.toFile());

        IProvider<ClassNode> dependencyProvider = null;
        for (Path path : options.dependencies) {
            System.out.printf("Reading Library %s\n", path.toAbsolutePath());
            IProvider<ClassNode> provider = JarReader.read(path.toFile()).getClasses();
            dependencyProvider = dependencyProvider == null ? provider : dependencyProvider.withFallback(provider);
        }

        var transformers = new ArrayList<ITransformer>();

        System.out.println("Loading Plugins");
        options.plugins.forEach(id -> PluginDiscovery.getPlugin(id).ifPresentOrElse(
            plugin -> transformers.addAll(getTransformers(plugin, jarFileProvider)),
            () -> System.out.printf("Unable to find plguin with ID %s, ignoring.\n", id)
        ));

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

        final var provider = dependencyProvider == null
            ? jarFileProvider.getClasses()
            : jarFileProvider.getClasses().withFallback(dependencyProvider);

        System.out.println("Writing Jar File");
        JarWriter.write(options.output.toFile(), jarFileProvider, () -> new ContextClassWriter(options.computeFrames ? COMPUTE_FRAMES : COMPUTE_MAXS, provider));
    }

    private List<ITransformer> getTransformers(IPlugin plugin, IJarFileProvider provider) {
        return plugin.getTransformers().stream().map(p -> p.provide(() -> provider)).collect(Collectors.toList());
    }
}
