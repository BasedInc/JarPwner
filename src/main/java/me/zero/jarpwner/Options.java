package me.zero.jarpwner;

import java.nio.file.Path;
import java.util.List;

/**
 * @author Brady
 * @since 3/21/2020
 */
public final class Options {

    public final Path input;
    public final Path output;
    public final List<Path> dependencies;
    public final List<String> plugins;
    public final boolean computeFrames;

    public Options(Path input, Path output, List<Path> dependencies, List<String> plugins, boolean computeFrames) {
        this.input = input;
        this.output = output;
        this.dependencies = dependencies;
        this.plugins = plugins;
        this.computeFrames = computeFrames;
    }
}
