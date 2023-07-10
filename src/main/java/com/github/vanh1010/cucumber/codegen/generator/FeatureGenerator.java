package com.github.vanh1010.cucumber.codegen.generator;

import java.util.List;

import com.github.vanh1010.cucumber.codegen.generator.joiner.Joiner;
import com.github.vanh1010.cucumber.codegen.generator.joiner.PascalCaseJoiner;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedFeature;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedPickle;

import io.cucumber.core.gherkin.Feature;

public record FeatureGenerator(PickleGenerator pickleGenerator) implements Generator<Feature, SuggestedFeature> {

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
                .map(pickleGenerator::generate)
                .toList();
    }
}
