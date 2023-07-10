package com.github.vanh1010.cucumber.codegen.gherkin;

import java.util.List;

public record SuggestedStep(SuggestedAnnotation annotation,
        String name,
        List<SuggestedParameter> parameters,
        String implementation) {
}
