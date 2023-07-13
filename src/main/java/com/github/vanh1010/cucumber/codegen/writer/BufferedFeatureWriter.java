package com.github.vanh1010.cucumber.codegen.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.github.vanh1010.cucumber.codegen.generator.Generator;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedFeature;

import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;

/**
 * Write stuffs to file
 */
public class BufferedFeatureWriter implements FileWriter<SuggestedFeature> {

    private static final String JAVA_FILE_EXTENSION = ".java";
    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedFeatureWriter.class);

    private final Options options;
    private final Generator<SuggestedFeature, String> generator;

    public BufferedFeatureWriter(Options options, Generator<SuggestedFeature, String> generator) {
        this.options = options;
        this.generator = generator;
    }

    /**
     * Writes a single suggested feature into a .java file.
     * The feature's name will be used as the class name.
     */
    @Override
    public void write(SuggestedFeature suggestedFeature) {
        Path outputDir = options.getOutputDir();
        outputDir.toFile().mkdirs();
        String featureName = suggestedFeature.name();
        Path featureFilePath = outputDir.resolve(featureName + JAVA_FILE_EXTENSION);
        try (BufferedWriter writer = Files.newBufferedWriter(featureFilePath, StandardOpenOption.CREATE_NEW)) {
            writer.write(generator.generate(suggestedFeature));
            LOGGER.info(() -> "Write to file " + featureFilePath);
        } catch (IOException ioe) {
            LOGGER.warn(() -> "Cannot write to file " + featureFilePath);
        }
    }
}
