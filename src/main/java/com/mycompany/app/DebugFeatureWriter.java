package com.mycompany.app;

import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;

public class DebugFeatureWriter implements FileWriter<SuggestedFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugFeatureWriter.class);

    @Override
    public void write(SuggestedFeature suggestedFeature) {
        LOGGER.info(suggestedFeature::generateJava);
    }
}
