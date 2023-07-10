package com.github.vanh1010.cucumber.codegen.backend.newbackend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.vanh1010.cucumber.codegen.backend.BackendException;
import com.github.vanh1010.cucumber.codegen.backend.CoreStepDefinition;
import com.github.vanh1010.cucumber.codegen.backend.StepDefinitionMatch;
import com.github.vanh1010.cucumber.codegen.backend.StepExpression;
import com.github.vanh1010.cucumber.codegen.backend.StepExpressionFactory;
import com.github.vanh1010.cucumber.codegen.logging.Logger;
import com.github.vanh1010.cucumber.codegen.logging.LoggerFactory;

import io.cucumber.core.backend.DataTableTypeDefinition;
import io.cucumber.core.backend.DefaultDataTableCellTransformerDefinition;
import io.cucumber.core.backend.DefaultDataTableEntryTransformerDefinition;
import io.cucumber.core.backend.DefaultParameterTransformerDefinition;
import io.cucumber.core.backend.DocStringTypeDefinition;
import io.cucumber.core.backend.ParameterTypeDefinition;
import io.cucumber.core.backend.StepDefinition;
import io.cucumber.core.gherkin.Step;
import io.cucumber.core.stepexpression.Argument;
import io.cucumber.core.stepexpression.StepTypeRegistry;

public class StepDefinitionGlue {

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

    public void addStepDefinition(StepDefinition stepDefinition) {
        stepDefinitions.add(stepDefinition);
    }

    public void addParameterType(ParameterTypeDefinition parameterType) {
        parameterTypeDefinitions.add(parameterType);
    }

    public void addDataTableType(DataTableTypeDefinition dataTableType) {
        dataTableTypeDefinitions.add(dataTableType);
    }

    public void addDocStringType(DocStringTypeDefinition docStringType) {
        docStringTypeDefinitions.add(docStringType);
    }

    public void addDefaultParameterTransformer(
            DefaultParameterTransformerDefinition defaultParameterTransformer) {
        defaultParameterTransformerDefinitions.add(defaultParameterTransformer);

    }

    public void addDefaultDataTableEntryTransformer(
            DefaultDataTableEntryTransformerDefinition defaultDataTableEntryTransformer) {
        defaultDataTableEntryTransformerDefinitions.add(defaultDataTableEntryTransformer);
    }

    public void addDefaultDataTableCellTransformer(
            DefaultDataTableCellTransformerDefinition defaultDataTableCellTransformer) {
        defaultDataTableCellTransformerDefinitions.add(defaultDataTableCellTransformer);
    }

    public void prepare(StepTypeRegistry stepTypeRegistry) {
        StepExpressionFactory stepExpressionFactory = new StepExpressionFactory(stepTypeRegistry);

        // define the parameter types
        parameterTypeDefinitions.forEach(ptd -> stepTypeRegistry.defineParameterType(ptd.parameterType()));
        dataTableTypeDefinitions.forEach(dtd -> stepTypeRegistry.defineDataTableType(dtd.dataTableType()));
        docStringTypeDefinitions.forEach(dtd -> stepTypeRegistry.defineDocStringType(dtd.docStringType()));

        if (defaultParameterTransformerDefinitions.size() == 1) {
            var definition = defaultParameterTransformerDefinitions.get(0);
            var transformer = definition.parameterByTypeTransformer();
            stepTypeRegistry.setDefaultParameterTransformer(transformer);
        } else if (defaultParameterTransformerDefinitions.size() > 1) {
            throw new BackendException("Duplicated parameter by type transformer");
        }

        if (defaultDataTableEntryTransformerDefinitions.size() == 1) {
            var definition = defaultDataTableEntryTransformerDefinitions.get(0);
            var transformer = definition.tableEntryByTypeTransformer();
            stepTypeRegistry.setDefaultDataTableEntryTransformer(transformer);
        } else if (defaultDataTableEntryTransformerDefinitions.size() > 1) {
            throw new BackendException("Duplicated data table entry transformer");
        }

        if (defaultDataTableCellTransformerDefinitions.size() == 1) {
            var definition = defaultDataTableCellTransformerDefinitions.get(0);
            var transformer = definition.tableCellByTypeTransformer();
            stepTypeRegistry.setDefaultDataTableCellTransformer(transformer);
        } else if (defaultDataTableCellTransformerDefinitions.size() > 1) {
            throw new BackendException("Duplicated data table cell transformer");
        }

        stepDefinitions.forEach(stepDefinition -> {
            // create the expression
            StepExpression stepExpression = stepExpressionFactory.createExpression(stepDefinition);
            CoreStepDefinition coreStepDefinition = new CoreStepDefinition(stepExpression, stepDefinition);
            stepDefinitionsByPattern.put(coreStepDefinition.getExpression().getSource(), coreStepDefinition);
        });

    }

    /**
     * Tries to match a step with its definition.
     * 
     * @param step the step to be matched
     * @return a record object represents the match, if succeed; null otherwise
     */
    public StepDefinitionMatch stepDefinitionMatch(Step step) {
        StepDefinitionMatch cachedMatch = cachedStepDefinitionMatch(step);
        if (cachedMatch != null) {
            return cachedMatch;
        }
        return findStepDefinitionMatch(step);
    }

    private StepDefinitionMatch cachedStepDefinitionMatch(Step step) {
        String stepDefinitionPattern = stepPatternByStepText.get(step.getText());
        if (stepDefinitionPattern == null) {
            return null;
        }
        CoreStepDefinition coreStepDefinition = stepDefinitionsByPattern.get(stepDefinitionPattern);
        if (coreStepDefinition == null) {
            return null;
        }
        List<Argument> arguments = coreStepDefinition.matchArguments(step);
        return new StepDefinitionMatch(coreStepDefinition, step, arguments);
    }

    private StepDefinitionMatch findStepDefinitionMatch(Step step) {
        List<StepDefinitionMatch> matches = new ArrayList<>();
        for (CoreStepDefinition coreStepDefinition : stepDefinitionsByPattern.values()) {
            List<Argument> arguments = coreStepDefinition.matchArguments(step);
            if (arguments != null) {
                matches.add(new StepDefinitionMatch(coreStepDefinition, step, arguments));
            }
        }
        if (matches.isEmpty()) {
            return null;
        }
        if (matches.size() > 1) {
            throw new BackendException("Ambiguous matches. Candidates are: " + matches);
        }
        StepDefinitionMatch match = matches.get(0);
        stepPatternByStepText.put(step.getText(), matches.get(0).stepDefinition().getPattern());
        return match;
    }
}
