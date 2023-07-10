package com.github.vanh1010.cucumber.codegen.gherkin;

import java.util.List;
import java.util.stream.Stream;

public record SuggestedFeature(String name, List<SuggestedPickle> pickles) {

    public Stream<SuggestedStep> streamSteps() {
        return pickles
                .stream()
                .map(SuggestedPickle::steps)
                .flatMap(List::stream);
    }
}
