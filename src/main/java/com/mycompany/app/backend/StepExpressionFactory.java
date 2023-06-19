package com.mycompany.app.backend;

import io.cucumber.core.backend.StepDefinition;
import io.cucumber.core.exception.CucumberException;
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
        final Expression expression;
        try {
            expression = expressionFactory.createExpression(expressionString);
        } catch (UndefinedParameterTypeException e) {
            LOGGER.warn(() -> "Undefined parameter type: " + e.getUndefinedParameterTypeName());
            throw new CucumberException("""
                    Could not create a cucumber expression for '%s'.
                    It appears you did not register a parameter type.
                    """.formatted(expressionString), e);
        }
        return new StepExpression(expression);
    }
}
