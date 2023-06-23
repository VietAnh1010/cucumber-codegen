package com.mycompany.app.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.app.logging.Logger;
import com.mycompany.app.logging.LoggerFactory;

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
import io.cucumber.cucumberexpressions.ParameterByTypeTransformer;
import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.datatable.TableCellByTypeTransformer;
import io.cucumber.datatable.TableEntryByTypeTransformer;

public class StepDefinitionGlue implements Glue {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepDefinitionGlue.class);

    private List<StepDefinition> stepDefinitions = new ArrayList<>();
    private List<ParameterTypeDefinition> parameterTypeDefinitions = new ArrayList<>();
    private List<DataTableTypeDefinition> dataTableTypeDefinitions = new ArrayList<>();
    private List<DocStringTypeDefinition> docStringTypeDefinitions = new ArrayList<>();
    private List<DefaultParameterTransformerDefinition> defaultParameterTransformerDefinitions = new ArrayList<>();
    private List<DefaultDataTableEntryTransformerDefinition> defaultDataTableEntryTransformerDefinitions = new ArrayList<>();
    private List<DefaultDataTableCellTransformerDefinition> defaultDataTableCellTransformerDefinitions = new ArrayList<>();

    private Map<String, String> stepPatternByStepText = new HashMap<>();
    private Map<String, CoreStepDefinition> stepDefinitionsByPattern = new HashMap<>();

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
    public void addDataTableType(DataTableTypeDefinition dataTableType) {
        dataTableTypeDefinitions.add(dataTableType);
    }

    @Override
    public void addDocStringType(DocStringTypeDefinition docStringType) {
        docStringTypeDefinitions.add(docStringType);
    }

    @Override
    public void addDefaultParameterTransformer(
            DefaultParameterTransformerDefinition defaultParameterTransformer) {
        defaultParameterTransformerDefinitions.add(defaultParameterTransformer);

    }

    @Override
    public void addDefaultDataTableEntryTransformer(
            DefaultDataTableEntryTransformerDefinition defaultDataTableEntryTransformer) {
        defaultDataTableEntryTransformerDefinitions.add(defaultDataTableEntryTransformer);
    }

    @Override
    public void addDefaultDataTableCellTransformer(
            DefaultDataTableCellTransformerDefinition defaultDataTableCellTransformer) {
        defaultDataTableCellTransformerDefinitions.add(defaultDataTableCellTransformer);
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

    public void prepareGlue(StepTypeRegistry stepTypeRegistry) {
        StepExpressionFactory stepExpressionFactory = new StepExpressionFactory(stepTypeRegistry);
        // add all param type into the type registry
        parameterTypeDefinitions.forEach(parameterTypeDefinition -> {
            ParameterType<?> parameterType = parameterTypeDefinition.parameterType();
            stepTypeRegistry.defineParameterType(parameterType);
        });
        dataTableTypeDefinitions.forEach(dtd -> stepTypeRegistry.defineDataTableType(dtd.dataTableType()));
        docStringTypeDefinitions.forEach(dtd -> stepTypeRegistry.defineDocStringType(dtd.docStringType()));

        if (defaultParameterTransformerDefinitions.size() == 1) {
            DefaultParameterTransformerDefinition definition = defaultParameterTransformerDefinitions.get(0);
            ParameterByTypeTransformer transformer = definition.parameterByTypeTransformer();
            stepTypeRegistry.setDefaultParameterTransformer(transformer);
        } else if (defaultParameterTransformerDefinitions.size() > 1) {
            // TODO: refactor to use custom exception.
            throw new RuntimeException("Duplicated parameter by type transformer");
        }

        if (defaultDataTableEntryTransformerDefinitions.size() == 1) {
            DefaultDataTableEntryTransformerDefinition definition = defaultDataTableEntryTransformerDefinitions.get(0);
            TableEntryByTypeTransformer transformer = definition.tableEntryByTypeTransformer();
            stepTypeRegistry.setDefaultDataTableEntryTransformer(transformer);
        } else if (defaultDataTableEntryTransformerDefinitions.size() > 1) {
            // TODO: refactor this to use customer exception
            throw new RuntimeException("Duplicated data table entry transformer");
        }

        if (defaultDataTableCellTransformerDefinitions.size() == 1) {
            DefaultDataTableCellTransformerDefinition definition = defaultDataTableCellTransformerDefinitions.get(0);
            TableCellByTypeTransformer transformer = definition.tableCellByTypeTransformer();
            stepTypeRegistry.setDefaultDataTableCellTransformer(transformer);
        } else if (defaultDataTableCellTransformerDefinitions.size() > 1) {
            // TODO: refactor this to use customer exception, as well
            throw new RuntimeException("Duplicated data table cell transformer");
        }

        stepDefinitions.forEach(stepDefinition -> {
            // create the expression
            StepExpression stepExpression = stepExpressionFactory.createExpression(stepDefinition);
            CoreStepDefinition coreStepDefinition = new CoreStepDefinition(stepExpression, stepDefinition);
            // detech duplicate here!
            stepDefinitionsByPattern.put(coreStepDefinition.getExpression().getSource(), coreStepDefinition);
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
        String stepDefinitionPattern = stepPatternByStepText.get(step.getText());
        if (stepDefinitionPattern == null) {
            return null;
        }
        CoreStepDefinition coreStepDefinition = stepDefinitionsByPattern.get(stepDefinitionPattern);
        if (coreStepDefinition == null) {
            return null;
        }
        List<Argument> arguments = coreStepDefinition.matchArguments(step);
        return arguments;
    }

    private Object findStepDefinitionMatch(Step step) {
        LOGGER.info(stepDefinitionsByPattern.values()::toString);
        List<StepDefinition> matches = new ArrayList<>();
        for (CoreStepDefinition coreStepDefinition : stepDefinitionsByPattern.values()) {
            List<Argument> arguments = coreStepDefinition.matchArguments(step);
            if (arguments != null) {
                // TODO: fix!
                matches.add(coreStepDefinition);
            }
        }
        if (matches.isEmpty()) {
            return null;
        }
        if (matches.size() > 1) {
            // TODO: fix this: throw a new type of argument
            throw new RuntimeException("Ambiguous matches. Candidates are: " + matches);
        }
        stepPatternByStepText.put(step.getText(), matches.get(0).getPattern());
        return new Object();
    }
}
