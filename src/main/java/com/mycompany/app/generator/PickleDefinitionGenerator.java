package com.mycompany.app.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mycompany.app.SuggestedPickle;
import com.mycompany.app.SuggestedStep;
import com.mycompany.app.backend.StepDefinitionGlue;

import io.cucumber.core.gherkin.Pickle;
import io.cucumber.core.gherkin.Step;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.core.stepexpression.StepTypeRegistry;

public class PickleDefinitionGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PickleDefinitionGenerator.class);

    private final StepDefinitionGenerator stepDefinitionGenerator;
    private final StepDefinitionGlue stepDefinitionGlue;

    public PickleDefinitionGenerator(
            StepDefinitionGenerator stepDefinitionGenerator,
            StepDefinitionGlue stepDefinitionGlue) {
        this.stepDefinitionGenerator = stepDefinitionGenerator;
        this.stepDefinitionGlue = stepDefinitionGlue;
    }

    public SuggestedPickle generate(Pickle pickle) {
        StepTypeRegistry stepTypeRegistry = createTypeRegistryForPickle(pickle);
        stepDefinitionGlue.prepareGlue(stepTypeRegistry);
        stepDefinitionGenerator.registerNewParameterTypeRegistry(stepTypeRegistry.parameterTypeRegistry());
        List<SuggestedStep> suggestedSteps = new ArrayList<>();
        for (Step step : pickle.getSteps()) {
            var match = stepDefinitionGlue.stepDefinitionMatch(step);
            if (match != null) {
                LOGGER.info(() -> step.getText() + " matched, skip...");
                continue;
            } else {
                LOGGER.info(() -> "Generate code for " + step.getText());
            }
            SuggestedStep suggestedStep = stepDefinitionGenerator.generate(step);
            suggestedSteps.add(suggestedStep);
        }
        return new SuggestedPickle(suggestedSteps);
    }

    private StepTypeRegistry createTypeRegistryForPickle(Pickle pickle) {
        String language = pickle.getLanguage();
        @SuppressWarnings("deprecation")
        Locale locale = new Locale(language);
        return new StepTypeRegistry(locale);
    }
}
