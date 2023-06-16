package com.mycompany.app;

import java.util.Collection;
import java.util.stream.Collectors;

import io.cucumber.core.backend.Backend;
import io.cucumber.core.backend.ObjectFactory;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.core.runner.Options;

public class FeatureDefinitionGenerator {

    private static final String SEPARATOR = "\n\n";

    private Collection<? extends Backend> backends;
    private ObjectFactory objectFactory;
    private Options options;

    public FeatureDefinitionGenerator(Collection<? extends Backend> backends, Options runnerOptions,
            ObjectFactory objectFactory) {
        this.backends = backends;
        this.objectFactory = objectFactory;
        this.options = runnerOptions;
    }

    public SuggestedFeature generate(Feature feature) {
        return new SuggestedFeature(
                featureName(feature),
                featureContent(feature));
    }

    private String featureName(Feature feature) {
        Joiner joiner = new PascalCaseJoiner();
        IdentifierGenerator featureNamGenerator = new IdentifierGenerator(joiner);
        return feature.getName()
                .map(featureNamGenerator::generate)
                .filter(s -> !s.isEmpty())
                .orElseThrow(() -> new RuntimeException("cannot generate a name for the feature"));
    }

    private String featureContent(Feature feature) {
        PickleDefinitionGenerator pickleGenerator = new PickleDefinitionGenerator(backends);
        return feature.getPickles()
                .stream()
                .map(pickleGenerator::generate)
                .collect(Collectors.joining(SEPARATOR));
    }
}
