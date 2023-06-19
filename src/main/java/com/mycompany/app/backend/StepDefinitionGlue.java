package com.mycompany.app.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.cucumber.core.backend.DataTableTypeDefinition;
import io.cucumber.core.backend.DefaultDataTableCellTransformerDefinition;
import io.cucumber.core.backend.DefaultDataTableEntryTransformerDefinition;
import io.cucumber.core.backend.DefaultParameterTransformerDefinition;
import io.cucumber.core.backend.DocStringTypeDefinition;
import io.cucumber.core.backend.Glue;
import io.cucumber.core.backend.HookDefinition;
import io.cucumber.core.backend.ParameterTypeDefinition;
import io.cucumber.core.backend.StaticHookDefinition;
import io.cucumber.core.backend.StepDefinition;
import io.cucumber.core.gherkin.Step;
import io.cucumber.core.stepexpression.Argument;
import io.cucumber.core.stepexpression.StepTypeRegistry;
import io.cucumber.cucumberexpressions.ParameterType;

public class StepDefinitionGlue implements Glue {

    private List<StepDefinition> stepDefinitions = new ArrayList<>();
    private List<ParameterTypeDefinition> parameterTypeDefinitions = new ArrayList<>();
    private Map<String, String> stepPatternByStepText = new HashMap<>();
    private Map<String, TrivalStepDefinition> stepDefinitionsByPattern = new HashMap<>();

    public StepDefinitionGlue() {

    }

    @Override
    public void addStepDefinition(StepDefinition stepDefinition) {
        stepDefinitions.add(stepDefinition);
    }

    @Override
    public void addParameterType(ParameterTypeDefinition parameterType) {
        parameterTypeDefinitions.add(parameterType);
    }

    @Override
    public void addBeforeAllHook(StaticHookDefinition beforeAllHook) {
    }

    @Override
    public void addAfterAllHook(StaticHookDefinition afterAllHook) {
    }

    @Override
    public void addBeforeHook(HookDefinition beforeHook) {
    }

    @Override
    public void addAfterHook(HookDefinition afterHook) {
    }

    @Override
    public void addBeforeStepHook(HookDefinition beforeStepHook) {
    }

    @Override
    public void addAfterStepHook(HookDefinition afterStepHook) {
    }

    @Override
    public void addDataTableType(DataTableTypeDefinition dataTableType) {
    }

    @Override
    public void addDefaultParameterTransformer(DefaultParameterTransformerDefinition defaultParameterTransformer) {
    }

    @Override
    public void addDefaultDataTableEntryTransformer(
            DefaultDataTableEntryTransformerDefinition defaultDataTableEntryTransformer) {
    }

    @Override
    public void addDefaultDataTableCellTransformer(
            DefaultDataTableCellTransformerDefinition defaultDataTableCellTransformer) {
    }

    @Override
    public void addDocStringType(DocStringTypeDefinition docStringType) {
    }

    public void prepareGlue(StepTypeRegistry stepTypeRegistry) {
        StepExpressionFactory stepExpressionFactory = new StepExpressionFactory(stepTypeRegistry);
        // add all param type into the type registry
        parameterTypeDefinitions.forEach(parameterTypeDefinition -> {
            ParameterType<?> parameterType = parameterTypeDefinition.parameterType();
            stepTypeRegistry.defineParameterType(parameterType);
        });

        stepDefinitions.forEach(stepDefinition -> {
            // create the expression
            StepExpression stepExpression = stepExpressionFactory.createExpression(stepDefinition);
            // TODO: fix this
            if (stepDefinition instanceof TrivalStepDefinition tsd) {
                tsd.setStepExpression(stepExpression);
            } else {
                throw new RuntimeException("Unsupported step definition type: " + stepDefinition.getClass().getName());
            }
        });
    }

    /**
     * Matches a step with its definition.
     */
    public Object stepDefinitionMatch(Step step) {
        var cachedMatch = cachedStepDefinitionMatch(step);
        if (cachedMatch != null) {
            return cachedMatch;
        }
        return findStepDefinitionMatch(step);
    }

    private Object cachedStepDefinitionMatch(Step step) {
        // TODO:
        String stepDefinitionPattern = stepPatternByStepText.get(step.getText());
        if (stepDefinitionPattern == null) {
            return null;
        }
        TrivalStepDefinition trivalStepDefinition = stepDefinitionsByPattern.get(stepDefinitionPattern);
        if (trivalStepDefinition == null) {
            return null;
        }
        List<Argument> arguments = trivalStepDefinition.matchedArguments(step);
        return new Object();
    }

    private Object findStepDefinitionMatch(Step step) {
        List<StepDefinition> matches = new ArrayList<>();
        for (TrivalStepDefinition trivalStepDefinition : stepDefinitionsByPattern.values()) {
            List<Argument> arguments = trivalStepDefinition.matchedArguments(step);
            if (arguments != null) {
                // TODO: fix!
                matches.add(trivalStepDefinition);
            }
        }
        if (matches.isEmpty()) {
            return null; // FAIL to match
        }
        if (matches.size() > 1) {
            // TODO: fix this: throw a new type of argument
            throw new RuntimeException("Ambiguous matches. Candidates are: " + matches);
        }
        stepPatternByStepText.put(step.getText(), matches.get(0).getPattern());
        // TODO: return the appropriate type
        return new Object();
    }
}
