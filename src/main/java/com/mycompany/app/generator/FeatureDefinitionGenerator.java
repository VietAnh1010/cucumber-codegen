package com.mycompany.app.generator;

import java.util.List;

import com.mycompany.app.SuggestedFeature;
import com.mycompany.app.SuggestedPickle;
import com.mycompany.app.joiner.Joiner;
import com.mycompany.app.joiner.PascalCaseJoiner;

import io.cucumber.core.gherkin.Feature;

public class FeatureDefinitionGenerator implements Generator<Feature, SuggestedFeature> {

    private final PickleDefinitionGenerator pickleDefinitionGenerator;

    public FeatureDefinitionGenerator(PickleDefinitionGenerator pickleDefinitionGenerator) {
        this.pickleDefinitionGenerator = pickleDefinitionGenerator;
    }

    @Override
    public SuggestedFeature generate(Feature feature) {
        String name = featureName(feature);
        List<SuggestedPickle> pickles = featurePickles(feature);
        return new SuggestedFeature(name, pickles);
    }

    private String featureName(Feature feature) {
        Joiner joiner = new PascalCaseJoiner();
        IdentifierGenerator featureNamGenerator = new IdentifierGenerator(joiner);
        return feature.getName()
                .map(featureNamGenerator::generate)
                .filter(s -> !s.isEmpty())
                .orElseThrow(() -> new GenerationFailureException("Cannot generate name for feature: " + feature));
    }

    private List<SuggestedPickle> featurePickles(Feature feature) {
        return feature.getPickles()
                .stream()
                .map(pickleDefinitionGenerator::generate)
                .toList();
    }
}
