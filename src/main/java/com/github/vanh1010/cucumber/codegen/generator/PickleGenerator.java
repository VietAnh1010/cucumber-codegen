package com.github.vanh1010.cucumber.codegen.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.vanh1010.cucumber.codegen.backend.StepDefinitionGlue;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedPickle;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedStep;

import io.cucumber.core.gherkin.Pickle;
import io.cucumber.core.gherkin.Step;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.core.stepexpression.StepTypeRegistry;

public class PickleGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PickleGenerator.class);

    private final StepGenerator stepDefinitionGenerator;
    private final StepDefinitionGlue stepDefinitionGlue;

    public PickleGenerator(
            StepGenerator stepDefinitionGenerator,
            StepDefinitionGlue stepDefinitionGlue) {
        this.stepDefinitionGenerator = stepDefinitionGenerator;
        this.stepDefinitionGlue = stepDefinitionGlue;
    }

    public SuggestedPickle generate(Pickle pickle) {
        StepTypeRegistry stepTypeRegistry = createTypeRegistryForPickle(pickle);
        stepDefinitionGlue.prepare(stepTypeRegistry);
        stepDefinitionGenerator.prepareRegistry(stepTypeRegistry.parameterTypeRegistry());
        List<SuggestedStep> suggestedSteps = new ArrayList<>();
        for (Step step : pickle.getSteps()) {
            if (stepDefinitionGlue.stepDefinitionMatch(step) != null) {
                LOGGER.debug(() -> step.getText() + " matched, skip...");
                continue;
            }
            LOGGER.debug(() -> "Generate code for " + step.getText());
            SuggestedStep suggestedStep = stepDefinitionGenerator.generate(step);
            suggestedSteps.add(suggestedStep);
        }
        return new SuggestedPickle(suggestedSteps);
    }

    private StepTypeRegistry createTypeRegistryForPickle(Pickle pickle) {
        String language = pickle.getLanguage();
        Locale locale = Locale.forLanguageTag(language);
        return new StepTypeRegistry(locale);
    }
}
