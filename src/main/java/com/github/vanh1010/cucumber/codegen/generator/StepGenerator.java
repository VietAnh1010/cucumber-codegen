package com.github.vanh1010.cucumber.codegen.generator;

import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedStep;

import io.cucumber.core.gherkin.Step;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;

public interface StepGenerator extends Generator<Step, SuggestedStep> {

    public void prepareRegistry(ParameterTypeRegistry registry);
}
