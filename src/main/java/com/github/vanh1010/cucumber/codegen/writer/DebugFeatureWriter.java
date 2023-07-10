package com.github.vanh1010.cucumber.codegen.writer;

import com.github.vanh1010.cucumber.codegen.generator.Generator;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedFeature;

import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;

public class DebugFeatureWriter implements FileWriter<SuggestedFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugFeatureWriter.class);

    private final Generator<SuggestedFeature, ?> generator;

    public DebugFeatureWriter(Generator<SuggestedFeature, ?> generator) {
        this.generator = generator;
    }

    @Override
    public void write(SuggestedFeature suggestedFeature) {
        LOGGER.info(() -> """
                FOR:

                %s

                GENEATED CODE:

                %s

                """.formatted(suggestedFeature, generator.generate(suggestedFeature)));
    }
}
