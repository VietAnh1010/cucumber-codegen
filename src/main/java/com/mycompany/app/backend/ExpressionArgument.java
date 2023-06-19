package com.mycompany.app.backend;

import io.cucumber.core.stepexpression.Argument;

public class ExpressionArgument implements Argument {
    
    private final io.cucumber.cucumberexpressions.Argument<?> argument;

    public ExpressionArgument(io.cucumber.cucumberexpressions.Argument<?> argument) {
        this.argument = argument;
    }

    @Override
    public Object getValue() {
        return argument.getValue();
    }

    public io.cucumber.cucumberexpressions.Argument<?> getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        return argument.getGroup() == null ? null : argument.getGroup().getValue();
    }
}
