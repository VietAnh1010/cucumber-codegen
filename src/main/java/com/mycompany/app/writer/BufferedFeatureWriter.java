package com.mycompany.app.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.mycompany.app.SuggestedFeature;
import com.mycompany.app.generator.Generator;

import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;

/**
 * Write stuffs to file
 */
public class BufferedFeatureWriter implements FileWriter<SuggestedFeature> {

    private static final Path DEFAULT_OUTPUT_PATH = Path.of("out");
    private static final String JAVA_FILE_EXTENSION = ".java";
    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedFeatureWriter.class);

    private final Path baseDir;
    private final Generator<SuggestedFeature, String> generator;

    public BufferedFeatureWriter(Path baseDir, Generator<SuggestedFeature, String> generator) {
        this.baseDir = baseDir;
        this.generator = generator;
    }

    public BufferedFeatureWriter(Generator<SuggestedFeature, String> generator) {
        this(DEFAULT_OUTPUT_PATH, generator);
    }

    /**
     * Writes a single suggested feature into a .java file.
     * The feature's name will be used as the class name. 
     */
    @Override
    public void write(SuggestedFeature suggestedFeature) {
        if (!suggestedFeature.hasPickles()) {
            return;
        }
        baseDir.toFile().mkdirs();
        String featureName = suggestedFeature.getName();
        Path featureFilePath = baseDir.resolve(featureName + JAVA_FILE_EXTENSION);        
        try (BufferedWriter writer = Files.newBufferedWriter(featureFilePath, StandardOpenOption.CREATE_NEW)) {
            writer.write(generator.generate(suggestedFeature));
            LOGGER.info(() -> "Write to file " + featureFilePath);
        } catch (IOException ioe) {
            LOGGER.warn(() -> "Cannot write to file " + featureFilePath);
        }
    }
}
