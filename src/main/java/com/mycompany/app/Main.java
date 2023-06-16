package com.mycompany.app;

import io.cucumber.core.cli.CommandlineOptions;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.core.options.CommandlineOptionsParser;
import io.cucumber.core.options.Constants;
import io.cucumber.core.options.CucumberProperties;
import io.cucumber.core.options.CucumberPropertiesParser;
import io.cucumber.core.options.RuntimeOptions;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * Cucumber Main. Runs Cucumber as a CLI.
 * <p>
 * Options can be provided in by (order of precedence):
 * <ol>
 * <li>Command line arguments</li>
 * <li>Properties from {@link System#getProperties()}</li>
 * <li>Properties from in {@link System#getenv()}</li>
 * <li>Properties from {@value Constants#CUCUMBER_PROPERTIES_FILE_NAME}</li>
 * </ol>
 * For available properties see {@link Constants}. For Command line options
 * {@link CommandlineOptions}.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String... argv) {
        byte exitStatus = run(argv, Thread.currentThread().getContextClassLoader());
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
    public static byte run(String... argv) {
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
    public static byte run(String[] argv, ClassLoader classLoader) {
        // we should build the builder here!
        LOGGER.info(() -> "Start running my own code ...");
        RuntimeOptions propertiesFileOptions = new CucumberPropertiesParser()
                .parse(CucumberProperties.fromPropertiesFile())
                .build();

        RuntimeOptions environmentOptions = new CucumberPropertiesParser()
                .parse(CucumberProperties.fromEnvironment())
                .build(propertiesFileOptions);

        RuntimeOptions systemOptions = new CucumberPropertiesParser()
                .parse(CucumberProperties.fromSystemProperties())
                .build(environmentOptions);

        CommandlineOptionsParser commandlineOptionsParser = new CommandlineOptionsParser(System.out);
        RuntimeOptions runtimeOptions = commandlineOptionsParser
                .parse(argv)
                .addDefaultGlueIfAbsent()
                .addDefaultFeaturePathIfAbsent()
                .addDefaultSummaryPrinterIfNotDisabled()
                .enablePublishPlugin()
                .build(systemOptions);

        Optional<Byte> exitStatus = commandlineOptionsParser.exitStatus();
        if (exitStatus.isPresent()) {
            return exitStatus.get();
        }

        final Runtime runtime = Runtime.builder()
                .withClassLoader(() -> classLoader)
                .withRuntimeOptions(runtimeOptions)
                .build();

        runtime.run();
        return 0;
    }

}
