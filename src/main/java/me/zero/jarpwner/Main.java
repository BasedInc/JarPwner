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
public class Main {

    public static void main(String[] args) throws IOException {
        final var optionParser = new OptionParser();

        final var helpOption = optionParser.acceptsAll(Arrays.asList("h", "help"))
            .forHelp();

        final var inputOption = optionParser.acceptsAll(Arrays.asList("in", "input"), "The input jar to be processed")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter(PathProperties.READABLE))
            .required();

        final var outputOption = optionParser.acceptsAll(Arrays.asList("out", "output"), "The output location for the processed jar")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter())
            .defaultsTo(Path.of("output.jar"));

        final var classPathOption = optionParser.acceptsAll(Arrays.asList("c", "classpath"), "Dependencies for the input jar, usually only required when compute frames is enabled")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter(PathProperties.READABLE));

        final var pluginOption = optionParser.acceptsAll(Arrays.asList("p", "plugin"), "Dependencies for the input jar, usually only required when compute frames is enabled")
            .withRequiredArg()
            .required();

        final var computeFramesOption = optionParser.acceptsAll(Arrays.asList("compute-frames", "frames"), "Enable COMPUTE_FRAMES flag when writing classes")
            .withOptionalArg()
            .ofType(Boolean.TYPE)
            .defaultsTo(false);

        final OptionSet optionSet;
        try {
            optionSet = optionParser.parse(args);
        } catch (OptionException e) {
            System.err.println(e.toString());
            System.exit(1);
            return;
        }

        if (optionSet.has(helpOption)) {
            try {
                optionParser.printHelpOn(System.out);
            } catch (IOException ignored) {}
            System.exit(0);
            return;
        }

        JarPwner.INSTANCE.run(new Options(
            inputOption.value(optionSet),
            outputOption.value(optionSet),
            classPathOption.values(optionSet),
            pluginOption.values(optionSet),
            computeFramesOption.value(optionSet)
        ));
    }
}
