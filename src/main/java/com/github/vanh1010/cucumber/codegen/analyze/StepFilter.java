package com.github.vanh1010.cucumber.codegen.analyze;

import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedStep;

@FunctionalInterface
public interface StepFilter {

    public boolean test(SuggestedStep step);
}
