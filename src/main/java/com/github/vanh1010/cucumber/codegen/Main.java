package com.github.vanh1010.cucumber.codegen;

import java.util.Map;

import com.github.vanh1010.cucumber.codegen.logging.Logger;
import com.github.vanh1010.cucumber.codegen.logging.LoggerFactory;
import com.github.vanh1010.cucumber.codegen.utils.PropertiesUtils;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String... argv) {
        int exitStatus = run(argv, Thread.currentThread().getContextClassLoader());
        System.exit(exitStatus);
    }

    /**
     * Launches the Cucumber-JVM command line.
     *
     * @param argv runtime options. See details in the
     *             {@code cucumber.api.cli.Usage.txt} resource.
     * @return 0 if execution was successful, 1 if it was not (test
     *         failures)
     */
    public static int run(String... argv) {
        return run(argv, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Launches the Cucumber-JVM command line.
     *
     * @param argv        runtime options. See details in the
     *                    {@code cucumber.api.cli.Usage.txt} resource.
     * @param classLoader classloader used to load the runtime
     * @return 0 if execution was successful, 1 if it was not (test
     *         failures)
     */
    public static int run(String[] argv, ClassLoader classLoader) {
        // we should build the builder here!
        LOGGER.info(() -> "Start running...");
        try {
            RuntimeOptions.Builder runtimeOptionsBuilder = RuntimeOptions.builder();
            Map<String, String> properties = PropertiesUtils.fromAllSources();
            OptionsParser optionsParser = new OptionsParser(runtimeOptionsBuilder, properties);
            optionsParser.parse();
            runtimeOptionsBuilder
                    .addDefaultAnnotations()
                    .addDefaultFeaturePathIfAbsent()
                    .addDefaultGluePathIfAbsent();
            RuntimeOptions runtimeOptions = runtimeOptionsBuilder.build();
            Runtime runtime = Runtime.builder()
                    .classLoader(() -> classLoader)
                    .runtimeOptions(runtimeOptions)
                    .build();
            runtime.run();
        } catch (Exception ex) {
            LOGGER.error(ex, () -> "Error while running the application");
            return 1;
        }
        return 0;
    }

}
