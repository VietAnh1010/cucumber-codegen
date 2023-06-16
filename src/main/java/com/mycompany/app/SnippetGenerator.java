package com.mycompany.app;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

public class SnippetGenerator {
    // based on snippet generator
    private static final ArgumentPattern DEFAULT_ARGUMENT_PATTERN = new ArgumentPattern(Pattern.compile("\\{.*?\\}"));

    private final Snippet snippet;
    private final CucumberExpressionGenerator generator;
    private final ImplementationGenerator implementation;

    public SnippetGenerator(
            Snippet snippet, 
            ParameterTypeRegistry parameterTypeRegistry,
            ImplementationGenerator implementation) {
        this.snippet = snippet;
        this.generator = new CucumberExpressionGenerator(parameterTypeRegistry);
        this.implementation = implementation;
    }

    // TODO: retain for now, remove in the future
    public SnippetGenerator(
        Snippet snippet,
        ParameterTypeRegistry parameterTypeRegistry) {
            this(snippet, parameterTypeRegistry, new TrivalImplementationGenerator());
    }

    public SuggestedSnippet generate(Step step) {
        List<GeneratedExpression> generatedExpressions = generator.generateExpressions(step.getText());
        // take the best expression, and a new snippet with that
        // TODO: should not return null
        if (generatedExpressions.isEmpty())
            return null;
        GeneratedExpression bestGeneratedExpression = generatedExpressions.get(0);
        Joiner joiner = new CamelCaseJoiner();
        IdentifierGenerator functionNameGenerator = new IdentifierGenerator(joiner);
        IdentifierGenerator parameterNameGenerator = new IdentifierGenerator(joiner);
        return createSnippet(step, functionNameGenerator, parameterNameGenerator, bestGeneratedExpression);
    }

    private SuggestedSnippet createSnippet(
            Step step,
            IdentifierGenerator functionNameGenerator,
            IdentifierGenerator parameterNameGenerator,
            GeneratedExpression expression) {
        String keyword = step.getType().isGivenWhenThen()
                ? step.getKeyword()
                : step.getPreviousGivenWhenThenKeyword();
        String source = expression.getSource();
        String functionName = functionName(source, functionNameGenerator);
        List<String> parameterNames = parameterNames(expression, parameterNameGenerator);
        Map<String, Type> arguments = arguments(step, parameterNames, expression.getParameterTypes());
        // we defer this. Do not generate the snippet right away
        return new SuggestedSnippet(
                snippet,
                sanitize(keyword),
                snippet.escapePattern(source),
                functionName,
                snippet.arguments(arguments),
                implementation.generate(),
                tableHint(step));
    }

    // TODO: rewrite this
    private String functionName(String sentence, IdentifierGenerator functionNameGenerator) {
        return Stream.of(sentence)
                .map(DEFAULT_ARGUMENT_PATTERN::replaceMatchesWithSpace)
                .map(functionNameGenerator::generate)
                .filter(s -> !s.isEmpty())
                .findFirst()
                .orElseGet(() -> functionNameGenerator.generate(sentence));
    }

    // We cannot improve if we encounter such V

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
