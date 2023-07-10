package com.github.vanh1010.cucumber.codegen.backend;

import io.cucumber.core.backend.StepDefinition;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.core.stepexpression.StepTypeRegistry;
import io.cucumber.cucumberexpressions.Expression;
import io.cucumber.cucumberexpressions.ExpressionFactory;
import io.cucumber.cucumberexpressions.UndefinedParameterTypeException;

public final class StepExpressionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepExpressionFactory.class);

    private final ExpressionFactory expressionFactory;

    public StepExpressionFactory(StepTypeRegistry registry) {
        this.expressionFactory = new ExpressionFactory(registry.parameterTypeRegistry());
    }

    public StepExpression createExpression(StepDefinition stepDefinition) {
        String expressionString = stepDefinition.getPattern();
        Expression expression;
        try {
            expression = expressionFactory.createExpression(expressionString);
        } catch (UndefinedParameterTypeException ex) {
            LOGGER.warn(() -> "Undefined parameter type: " + ex.getUndefinedParameterTypeName());
            throw new BackendException("""
                    Could not create a cucumber expression for '%s'.
                    Did you forget to register a parameter type?
                    """.formatted(expressionString), ex);
        }
        return new StepExpression(expression);
    }
}
