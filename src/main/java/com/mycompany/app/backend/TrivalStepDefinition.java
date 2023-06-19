package com.mycompany.app.backend;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import io.cucumber.core.backend.CucumberBackendException;
import io.cucumber.core.backend.CucumberInvocationTargetException;
import io.cucumber.core.backend.ParameterInfo;
import io.cucumber.core.backend.StepDefinition;
import io.cucumber.core.gherkin.Step;
import io.cucumber.core.stepexpression.Argument;

public class TrivalStepDefinition implements StepDefinition {

    private final String expression; // should we rename this to pattern?
    private final List<ParameterInfo> parameterInfos;
    private StepExpression stepExpression = null;
    private Type[] types = null;

    public TrivalStepDefinition(String expression, Method method) {
        this.expression = expression;
        this.parameterInfos = TrivalParameterInfo.fromMethod(method);
    }

    @Override
    public boolean isDefinedAt(StackTraceElement stackTraceElement) {
        throw new UnsupportedOperationException("Unimplemented method 'isDefinedAt'");
    }

    @Override
    public String getLocation() {
        throw new UnsupportedOperationException("Unimplemented method 'getLocation'");
    }

    @Override
    public void execute(Object[] args) throws CucumberBackendException, CucumberInvocationTargetException {
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public List<ParameterInfo> parameterInfos() {
        return parameterInfos;
    }

    @Override
    public String getPattern() {
        return expression;
    }

    public void setStepExpression(StepExpression stepExpression) {
        this.stepExpression = stepExpression;
    }

    public List<Argument> matchedArguments(Step step) {
        return stepExpression.match(step.getText(), types);
    }
}
