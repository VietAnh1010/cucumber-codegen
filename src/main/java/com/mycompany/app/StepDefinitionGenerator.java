package com.mycompany.app;

import io.cucumber.core.gherkin.Step;

public interface StepDefinitionGenerator {

    public SuggestedStep generate(Step step);
}
