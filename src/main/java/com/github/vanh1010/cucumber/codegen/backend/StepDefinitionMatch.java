package com.github.vanh1010.cucumber.codegen.backend;

import java.util.List;

import io.cucumber.core.backend.StepDefinition;
import io.cucumber.core.gherkin.Step;
import io.cucumber.core.stepexpression.Argument;

public record StepDefinitionMatch(StepDefinition stepDefinition, Step step, List<Argument> arguments) {
}
