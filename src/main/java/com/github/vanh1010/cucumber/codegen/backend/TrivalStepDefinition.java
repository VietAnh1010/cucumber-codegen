package com.github.vanh1010.cucumber.codegen.backend;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import io.cucumber.core.backend.CucumberBackendException;
import io.cucumber.core.backend.CucumberInvocationTargetException;
import io.cucumber.core.backend.ParameterInfo;
import io.cucumber.core.backend.StepDefinition;

public class TrivalStepDefinition implements StepDefinition {

    private final String pattern;
    private final List<ParameterInfo> parameterInfos;

    public TrivalStepDefinition(String pattern, Method method) {
        this.pattern = pattern;
        this.parameterInfos = TrivalParameterInfo.from(method);
    }

    public TrivalStepDefinition(String pattern, Type[] parameters) {
        this.pattern = pattern;
        this.parameterInfos = TrivalParameterInfo.from(parameters);
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
        return pattern;
    }
}
