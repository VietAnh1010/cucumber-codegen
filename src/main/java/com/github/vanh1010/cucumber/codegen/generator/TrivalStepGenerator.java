package com.github.vanh1010.cucumber.codegen.generator;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.vanh1010.cucumber.codegen.generator.joiner.CamelCaseJoiner;
import com.github.vanh1010.cucumber.codegen.generator.joiner.Joiner;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedAnnotation;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedParameter;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedStep;

import io.cucumber.core.gherkin.Argument;
import io.cucumber.core.gherkin.DataTableArgument;
import io.cucumber.core.gherkin.DocStringArgument;
import io.cucumber.core.gherkin.Step;
import io.cucumber.core.gherkin.StepType;
import io.cucumber.cucumberexpressions.CucumberExpressionGenerator;
import io.cucumber.cucumberexpressions.GeneratedExpression;
import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.plugin.event.Location;
import io.cucumber.plugin.event.StepArgument;

public class TrivalStepGenerator implements StepGenerator {

    private static final String TODO_INSTRUCTION = "// TODO: auto generated stub\n";
    private static final String THROW_CLAUSE = "throw new " + PendingException.class.getName() + "();";
    private static final ArgumentPattern DEFAULT_ARGUMENT_PATTERN = new ArgumentPattern(Pattern.compile("\\{.*?\\}"));

    private final Options options;
    private CucumberExpressionGenerator cucumberExpressionGenerator = null;

    public TrivalStepGenerator(Options options) {
        this.options = options;
    }

    @Override
    public void prepareRegistry(ParameterTypeRegistry parameterTypeRegistry) {
        cucumberExpressionGenerator = new CucumberExpressionGenerator(parameterTypeRegistry);
    }

    @Override
    public SuggestedStep generate(Step step) {
        List<GeneratedExpression> generatedExpressions = cucumberExpressionGenerator
                .generateExpressions(step.getText());
        if (generatedExpressions.isEmpty()) {
            throw new GenerationFailureException("No avaiable expression for step definition");
        }
        GeneratedExpression bestGeneratedExpression = generatedExpressions.get(0);
        Joiner joiner = new CamelCaseJoiner();
        IdentifierGenerator methodNameGenerator = new IdentifierGenerator(joiner);
        IdentifierGenerator parameterNameGenerator = new IdentifierGenerator(joiner);
        return createSuggestedStep(step, methodNameGenerator, parameterNameGenerator, bestGeneratedExpression);
    }

    private SuggestedStep createSuggestedStep(
            Step step,
            IdentifierGenerator methodNameGenerator,
            IdentifierGenerator parameterNameGenerator,
            GeneratedExpression expression) {
        String keyword = step.getType().isGivenWhenThen()
                ? step.getKeyword()
                : step.getPreviousGivenWhenThenKeyword();
        String source = expression.getSource();
        List<String> parameterNames = expression.getParameterNames()
                .stream()
                .map(parameterNameGenerator::generate)
                .toList();
        SuggestedAnnotation annotation = annotation(keyword, source);
        String methodName = methodName(source, methodNameGenerator);
        List<SuggestedParameter> parameters = parameters(step, parameterNames, expression.getParameterTypes());
        String implementation = implementation(step);
        return new SuggestedStep(annotation, methodName, parameters, implementation);
    }

    private SuggestedAnnotation annotation(String keyword, String pattern) {
        String sanitizedKeyword = keyword.replaceAll("[\\s',!]", "");
        var annotationClass = options.getAnnotations()
                .stream()
                .filter(clazz -> clazz.getSimpleName().equals(sanitizedKeyword))
                .findFirst()
                .orElseThrow(() -> new GenerationFailureException("Cannot find annotation for: " + keyword));
        String escapedPattern = pattern.replace("\\", "\\\\").replace("\"", "\\\"");
        return new SuggestedAnnotation(annotationClass, escapedPattern);
    }

    private String methodName(String sentence, IdentifierGenerator methodNameGenerator) {
        String methodName = methodNameGenerator.generate(
                DEFAULT_ARGUMENT_PATTERN.replaceMatchesWithSpace(sentence));
        if (methodName.isEmpty()) {
            throw new GenerationFailureException("Cannot generate function name for: " + sentence);
        }
        return methodName;
    }

    private List<SuggestedParameter> parameters(
            Step step,
            List<String> parameterNames,
            List<ParameterType<?>> parameterTypes) {
        List<SuggestedParameter> parameters = new ArrayList<>(parameterTypes.size() + 1);
        for (int i = 0; i < parameterTypes.size(); i++) {
            ParameterType<?> parameterType = parameterTypes.get(i);
            String parameterName = parameterNames.get(i);
            parameters.add(new SuggestedParameter(parameterType.getType(), parameterName));
        }
        StepArgument arg = step.getArgument();
        if (arg instanceof DocStringArgument) {
            String parameterName = avoidDuplication("docString", parameterNames);
            parameters.add(new SuggestedParameter(String.class, parameterName));
        } else if (arg instanceof DataTableArgument) {
            String parameterName = avoidDuplication("dataTable", parameterNames);
            parameters.add(new SuggestedParameter(DataTable.class, parameterName));
        }
        return parameters;
    }

    private String implementation(Step step) {
        return TODO_INSTRUCTION + tableHint(step) + THROW_CLAUSE;
    }

    private static String tableHint(Step step) {
        Argument argument = step.getArgument();
        if (argument instanceof DataTableArgument) {
            return """
                    // For automatic transformation, change DataTable to one of
                    // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
                    // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
                    // Double, Byte, Short, Long, BigInteger or BigDecimal.
                    //
                    // For other transformations you can register a DataTableType.
                    """;
        }
        return "";
    }

    private static String avoidDuplication(String name, List<String> parameterNames) {
        if (!parameterNames.contains(name)) {
            return name;
        }
        for (int i = 1;; i++) {
            String candidate = name + i;
            if (!parameterNames.contains(candidate)) {
                return candidate;
            }
        }
    }

    public static void main(String[] args) {
        var instance = new TrivalStepGenerator(new Options() {
            @Override
            public String getPackageName() {
                throw new UnsupportedOperationException("Unimplemented method 'getPackageName'");
            }

            @Override
            public Set<Class<? extends Annotation>> getAnnotations() {
                throw new UnsupportedOperationException("Unimplemented method 'getAnnotations'");
            }
        });
        var implementation = instance.implementation(new Step() {

            @Override
            public String getKeyword() {
                throw new UnsupportedOperationException("Unimplemented method 'getKeyword'");
            }

            @Override
            public String getText() {
                throw new UnsupportedOperationException("Unimplemented method 'getText'");
            }

            @Override
            public int getLine() {
                throw new UnsupportedOperationException("Unimplemented method 'getLine'");
            }

            @Override
            public Location getLocation() {
                throw new UnsupportedOperationException("Unimplemented method 'getLocation'");
            }

            @Override
            public StepType getType() {
                throw new UnsupportedOperationException("Unimplemented method 'getType'");
            }

            @Override
            public String getPreviousGivenWhenThenKeyword() {
                throw new UnsupportedOperationException("Unimplemented method 'getPreviousGivenWhenThenKeyword'");
            }

            @Override
            public String getId() {
                throw new UnsupportedOperationException("Unimplemented method 'getId'");
            }

            @Override
            public Argument getArgument() {
                return new DataTableArgument() {

                    @Override
                    public List<List<String>> cells() {
                        throw new UnsupportedOperationException("Unimplemented method 'cells'");
                    }

                    @Override
                    public int getLine() {
                        throw new UnsupportedOperationException("Unimplemented method 'getLine'");
                    }
                };
            }
        });
        System.out.println(implementation);
    }
}
