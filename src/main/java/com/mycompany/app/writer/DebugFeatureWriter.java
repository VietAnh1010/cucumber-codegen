package com.mycompany.app.writer;

import com.mycompany.app.SuggestedFeature;
import com.mycompany.app.generator.CodeGenerator;

import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;

public class DebugFeatureWriter implements FileWriter<SuggestedFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugFeatureWriter.class);

    private final CodeGenerator codeGenerator;

    public DebugFeatureWriter(CodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    @Override
    public void write(SuggestedFeature suggestedFeature) {
        LOGGER.info(suggestedFeature::toString);
        LOGGER.info(() -> codeGenerator.generateJavaCode(suggestedFeature));
    }
}
