package me.zero.jarpwner;

import me.zero.jarpwner.plugin.AllatoriExpiryTransformer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * @author Brady
 * @since 3/31/2019
 */
public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Reading Jar File");
        var input = new JarFileHelper();
        input.read(new File("input.jar"));

        var transformers = Collections.singletonList(
                new AllatoriExpiryTransformer()
        );

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
