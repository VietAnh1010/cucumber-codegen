package com.github.vanh1010.cucumber.codegen.utils;

import java.util.stream.Stream;

import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedFeature;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedStep;

public class SuggestedGherkinUtils {

    private SuggestedGherkinUtils() {
    }

    public static Stream<SuggestedStep> streamOfSteps(SuggestedFeature feature) {
        return feature.pickles()
                .stream()
                .flatMap(pickle -> pickle.steps().stream());
    }
}
