package com.github.vanh1010.cucumber.codegen.backend;

import java.lang.reflect.Type;
import java.util.List;

import io.cucumber.core.stepexpression.Argument;
import io.cucumber.cucumberexpressions.Expression;

/**
 * My own version of the step expresion
 */
public class StepExpression {

    private final Expression expression;

    public StepExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public Class<? extends Expression> getExpressionType() {
        return expression.getClass();
    }

    public String getSource() {
        return expression.getSource();
    }

    /**
     * Returns a list of arguments that match the given text with the provided type
     * hints.
     * 
     * @return an empty list if the match failed
     */
    public List<Argument> match(String text, Type... types) {
        List<io.cucumber.cucumberexpressions.Argument<?>> match = expression.match(text, types);
        if (match == null) {
            return null;
        }
        return match.stream().<Argument>map(ExpressionArgument::new).toList();
    }
}
