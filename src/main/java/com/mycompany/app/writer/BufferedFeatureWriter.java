package com.mycompany.app.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.mycompany.app.SuggestedFeature;
import com.mycompany.app.generator.CodeGenerator;

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

    public BufferedFeatureWriter(CodeGenerator codeGenerator) {
        // TODO: fix the path
        // Use Path.of(URI) so that we can pass an URI to make the codebase more consistent
        this(Path.of("out"), codeGenerator);
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
        // try to see whether the file exist or not
        String featureName = suggestedFeature.getName();
        Path featureFilePath = baseDir.resolve(featureName + JAVA_FILE_EXTENSION);
        
        CompilationUnit cu = createCompilationUnit(featureFilePath, suggestedFeature);
        try (BufferedWriter writer = Files.newBufferedWriter(featureFilePath, StandardOpenOption.CREATE_NEW)) {
            writer.write(cu.toString());
            LOGGER.info(() -> "Write to file " + featureFilePath);
        } catch (IOException ioe) {
            LOGGER.warn(() -> "Cannot write to file " + featureFilePath);
        }
    }

    private CompilationUnit createCompilationUnit(Path featureFilePath, SuggestedFeature suggestedFeature) {
        try {
            CompilationUnit cu = null;
            if (Files.exists(featureFilePath)) {
                cu = StaticJavaParser.parse(featureFilePath);
                /**
                 * Can we read feature from the compilation unit?
                 */
            } else {
                cu = codeGenerator.newFeatureFile(suggestedFeature);
            }
            return cu;
        } catch (IOException ioe) {
            LOGGER.warn(() -> "Cannot create AST for feature: " + suggestedFeature.getName());
            return null;
        }
    }
}
