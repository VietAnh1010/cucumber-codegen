package com.mycompany.app.backend;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import io.cucumber.core.backend.CucumberBackendException;
import io.cucumber.core.backend.CucumberInvocationTargetException;
import io.cucumber.core.backend.ParameterInfo;
import io.cucumber.core.backend.StepDefinition;
import io.cucumber.core.gherkin.Step;
import io.cucumber.core.stepexpression.Argument;

/**
 * Decorator pattern.
 * This class decorates a step definiton with extra functionalities.
 */
public class CoreStepDefinition implements StepDefinition {

    private final StepExpression stepExpression;
    private final StepDefinition stepDefinition;
    private final Type[] types;

    public CoreStepDefinition(StepExpression stepExpression, StepDefinition stepDefinition) {
        this.stepExpression = stepExpression;
        this.stepDefinition = stepDefinition;
        this.types = Objects.<List<ParameterInfo>>requireNonNullElseGet(stepDefinition.parameterInfos(), List::of)
                .stream()
                .map(ParameterInfo::getType)
                .toArray(Type[]::new);
    }

    @Override
    public boolean isDefinedAt(StackTraceElement stackTraceElement) {
        return stepDefinition.isDefinedAt(stackTraceElement);
    }

    @Override
    public String getLocation() {
        return stepDefinition.getLocation();
    }

    @Override
    public void execute(Object[] args) throws CucumberBackendException, CucumberInvocationTargetException {
        stepDefinition.execute(args);
    }

    @Override
    public List<ParameterInfo> parameterInfos() {
        return stepDefinition.parameterInfos();
    }

    @Override
    public String getPattern() {
        return stepDefinition.getPattern();
    }

    public List<Argument> matchArguments(Step step) {
        return stepExpression.match(step.getText(), types);
    }

    public StepExpression getExpression() {
        return stepExpression;
    }
}
