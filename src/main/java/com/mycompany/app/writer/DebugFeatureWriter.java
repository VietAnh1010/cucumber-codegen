package com.mycompany.app.writer;

import com.mycompany.app.SuggestedFeature;
import com.mycompany.app.generator.Generator;

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
        LOGGER.info(suggestedFeature::toString);
        LOGGER.info(() -> generator.generate(suggestedFeature).toString());
    }
}
