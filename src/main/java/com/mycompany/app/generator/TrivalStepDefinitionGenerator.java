package com.mycompany.app.generator;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.mycompany.app.SuggestedStep;
import com.mycompany.app.joiner.CamelCaseJoiner;
import com.mycompany.app.joiner.Joiner;

import io.cucumber.core.backend.Snippet;
import io.cucumber.core.gherkin.DataTableArgument;
import io.cucumber.core.gherkin.DocStringArgument;
import io.cucumber.core.gherkin.Step;
import io.cucumber.cucumberexpressions.CucumberExpressionGenerator;
import io.cucumber.cucumberexpressions.GeneratedExpression;
import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;
import io.cucumber.datatable.DataTable;
import io.cucumber.plugin.event.StepArgument;

public class TrivalStepDefinitionGenerator implements StepDefinitionGenerator {

    private static final String TODO_INSTRUCTION = "TODO: auto generated stub";
    private static final ArgumentPattern DEFAULT_ARGUMENT_PATTERN = new ArgumentPattern(Pattern.compile("\\{.*?\\}"));

    private final Snippet snippet;
    private CucumberExpressionGenerator cucumberExpressionGenerator;

    public TrivalStepDefinitionGenerator(Snippet snippet) {
        this.snippet = snippet;
    }

    public void registerNewParameterTypeRegistry(ParameterTypeRegistry parameterTypeRegistry) {
        cucumberExpressionGenerator = new CucumberExpressionGenerator(parameterTypeRegistry);
    }

    public SuggestedStep generate(Step step) {
        List<GeneratedExpression> generatedExpressions = cucumberExpressionGenerator
                .generateExpressions(step.getText());
        if (generatedExpressions.isEmpty()) {
            throw new RuntimeException("No avaiable expression to generate step definition?!");
        }
        GeneratedExpression bestGeneratedExpression = generatedExpressions.get(0);
        Joiner joiner = new CamelCaseJoiner();
        IdentifierGenerator functionNameGenerator = new IdentifierGenerator(joiner);
        IdentifierGenerator parameterNameGenerator = new IdentifierGenerator(joiner);
        return createSuggestedStep(step, functionNameGenerator, parameterNameGenerator, bestGeneratedExpression);
    }

    private SuggestedStep createSuggestedStep(
            Step step,
            IdentifierGenerator functionNameGenerator,
            IdentifierGenerator parameterNameGenerator,
            GeneratedExpression expression) {
        String keyword = step.getType().isGivenWhenThen()
                ? step.getKeyword()
                : step.getPreviousGivenWhenThenKeyword();
        String source = expression.getSource();
        String methodName = methodName(source, functionNameGenerator);
        List<String> parameterNames = parameterNames(expression, parameterNameGenerator);
        Map<String, Type> arguments = arguments(step, parameterNames, expression.getParameterTypes());
        return new SuggestedStep(snippet.template().format(new String[] {
                sanitize(keyword),
                snippet.escapePattern(source),
                methodName,
                snippet.arguments(arguments),
                TODO_INSTRUCTION,
                tableHint(step)
        }));
    }

    private String methodName(String sentence, IdentifierGenerator functionNameGenerator) {
        String methodName = functionNameGenerator.generate(
                DEFAULT_ARGUMENT_PATTERN.replaceMatchesWithSpace(sentence));
        if (methodName.isEmpty()) {
            throw new RuntimeException("Cannot generate function name for sentence: " + sentence);
        }
        return methodName;
    }

    private List<String> parameterNames(
            GeneratedExpression expression,
            IdentifierGenerator parameterNameGenerator) {
        return expression.getParameterNames()
                .stream()
                .map(parameterNameGenerator::generate)
                .toList();
    }

    private Map<String, Type> arguments(Step step, List<String> parameterNames, List<ParameterType<?>> parameterTypes) {
        Map<String, Type> arguments = new LinkedHashMap<>(parameterTypes.size() + 1);
        for (int i = 0; i < parameterTypes.size(); i++) {
            ParameterType<?> parameterType = parameterTypes.get(i);
            String parameterName = parameterNames.get(i);
            arguments.put(parameterName, parameterType.getType());
        }
        StepArgument arg = step.getArgument();
        if (arg == null) {
            return arguments;
        } else if (arg instanceof DocStringArgument) {
            arguments.put(parameterName("docString", parameterNames), String.class);
        } else if (arg instanceof DataTableArgument) {
            arguments.put(parameterName("dataTable", parameterNames), DataTable.class);
        }
        return arguments;
    }

    private static String sanitize(String keyWord) {
        return keyWord.replaceAll("[\\s',!]", "");
    }

    private String tableHint(Step step) {
        if (step.getArgument() == null) {
            return "";
        }
        if (step.getArgument() instanceof DataTableArgument) {
            return snippet.tableHint();
        }
        return "";
    }

    private String parameterName(String name, List<String> parameterNames) {
        if (!parameterNames.contains(name)) {
            return name;
        }
        for (int i = 1;; i++) {
            if (!parameterNames.contains(name + i)) {
                return name + i;
            }
        }
    }
}
