package com.mycompany.app.generator;

import com.mycompany.app.SuggestedStep;


import io.cucumber.core.gherkin.Step;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;

public interface StepDefinitionGenerator {

    public SuggestedStep generate(Step step);
    public void registerNewParameterTypeRegistry(ParameterTypeRegistry parameterTypeRegistry);
}
