package com.mycompany.app;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;

/**
 * Write stuffs to file
 */
public class BufferedFeatureWriter implements FileWriter<SuggestedFeature> {

    private static final String JAVA_FILE_EXTENSION = ".java";
    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedFeatureWriter.class);

    private final Path baseDir;
    private final CodeGenerator codeGenerator;

    public BufferedFeatureWriter(Path baseDir, CodeGenerator codeGenerator) {
        this.baseDir = baseDir;
        this.codeGenerator = codeGenerator;
    }

    BufferedFeatureWriter(CodeGenerator codeGenerator) {
        this(Path.of("out"), codeGenerator);
    }

    /**
     * Writes a single suggested feature into a .java file.
     * The feature's name will be used as the class name. 
     */
    @Override
    public void write(SuggestedFeature suggestedFeature) {
        baseDir.toFile().mkdirs();
        String featureName = suggestedFeature.getName();

        Path featureFilePath = baseDir.resolve(featureName + JAVA_FILE_EXTENSION);
        try (OutputStream stream = new BufferedOutputStream(
                Files.newOutputStream(
                        featureFilePath,
                        StandardOpenOption.CREATE_NEW))) {
            stream.write(codeGenerator.generateJavaCode(suggestedFeature).getBytes());
            LOGGER.info(() -> "Write to file " + featureFilePath);
        } catch (IOException ioe) {
            LOGGER.warn(() -> "Cannot write to file " + featureFilePath);
        }
    }
}