package me.zero.jarpwner;

import me.zero.jarpwner.asm.ContextClassWriter;
import me.zero.jarpwner.plugin.IPlugin;
import me.zero.jarpwner.plugin.PluginDiscovery;
import me.zero.jarpwner.transform.ITransformer;
import me.zero.jarpwner.util.jar.IJarFileProvider;
import me.zero.jarpwner.util.jar.JarReader;
import me.zero.jarpwner.util.jar.JarWriter;
import me.zero.jarpwner.util.provider.IProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;
import org.objectweb.asm.tree.ClassNode;

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

    private final Logger logger = LogManager.getLogger(JarPwner.class, new StringFormatterMessageFactory());

    public void run(Options options) throws IOException {
        this.logger.info("Reading Input %s", options.input.toAbsolutePath());
        final var jarFileProvider = JarReader.read(options.input.toFile());

        IProvider<ClassNode> dependencyProvider = null;
        for (Path path : options.dependencies) {
            this.logger.info("Reading Library %s", path.toAbsolutePath());
            IProvider<ClassNode> provider = JarReader.read(path.toFile()).getClasses();
            dependencyProvider = dependencyProvider == null ? provider : dependencyProvider.withFallback(provider);
        }

        final var transformers = new ArrayList<ITransformer>();

        this.logger.info("Loading Plugins");
        options.plugins.forEach(id -> PluginDiscovery.getPlugin(id).ifPresentOrElse(
            plugin -> transformers.addAll(getTransformers(plugin, jarFileProvider)),
            () -> this.logger.info("Unable to find plugin with ID %s, ignoring.", id)
        ));

        if (transformers.size() == 0) {
            this.logger.info("No transformers provided, exiting.");
            return;
        }

        this.logger.info("Running %d Transformer(s)", transformers.size());
        transformers.forEach(ITransformer::setup);
        transformers.forEach(transformer -> {
            jarFileProvider.getClasses().getAll().forEach((name, cn) -> {
                if (transformer.accepts(name)) {
                    transformer.apply(cn);
                }
            });

            transformer.getInfo().forEach(info ->
                this.logger.info("[%s] %s", transformer.getClass().getSimpleName(), info));
        });
        transformers.forEach(ITransformer::cleanup);

        final var provider = dependencyProvider == null
            ? jarFileProvider.getClasses()
            : jarFileProvider.getClasses().withFallback(dependencyProvider);

        this.logger.info("Writing Jar File");
        JarWriter.write(
            options.output.toFile(),
            jarFileProvider,
            () -> new ContextClassWriter(options.computeFrames ? COMPUTE_FRAMES : COMPUTE_MAXS, provider)
        );
    }

    public Logger getLogger() {
        return this.logger;
    }

    private List<ITransformer> getTransformers(IPlugin plugin, IJarFileProvider provider) {
        return plugin.getTransformers().stream().map(p -> p.provide(() -> provider)).collect(Collectors.toList());
    }
}
