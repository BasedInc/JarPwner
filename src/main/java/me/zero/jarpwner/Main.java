package me.zero.jarpwner;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Brady
 * @since 3/31/2019
 */
public final class Main {

    private Main() {}

    public static void main(String[] args) throws IOException {
        final var parser = new OptionParser();

        final var helpOption = parser.acceptsAll(Arrays.asList("h", "help"), "Displays this menu")
            .forHelp();

        final var inputOption = parser.acceptsAll(Arrays.asList("i", "in", "input"), "The input jar to be processed")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter(PathProperties.READABLE))
            .required();

        final var outputOption = parser.acceptsAll(Arrays.asList("o", "out", "output"), "The output location for the processed jar")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter())
            .defaultsTo(Path.of("output.jar"));

        final var classPathOption = parser.acceptsAll(Arrays.asList("c", "classpath"), "Dependencies for the input jar, usually only required when compute frames is enabled")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter(PathProperties.READABLE));

        final var pluginOption = parser.acceptsAll(Arrays.asList("p", "plugin"), "Dependencies for the input jar, usually only required when compute frames is enabled")
            .withRequiredArg()
            .required();

        final var computeFramesOption = parser.acceptsAll(Arrays.asList("f", "frames"), "Enable COMPUTE_FRAMES flag when writing classes");

        final OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException e) {
            System.err.println(e.toString());
            System.exit(1);
            return;
        }

        if (options.has(helpOption)) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException ignored) {}
            System.exit(0);
            return;
        }

        JarPwner.INSTANCE.run(new Options(
            inputOption.value(options),
            outputOption.value(options),
            classPathOption.values(options),
            pluginOption.values(options),
            options.has(computeFramesOption)
        ));
    }
}
