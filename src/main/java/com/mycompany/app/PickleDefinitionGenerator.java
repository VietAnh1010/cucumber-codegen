package com.mycompany.app;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import io.cucumber.core.backend.Backend;
import io.cucumber.core.gherkin.Pickle;
import io.cucumber.core.gherkin.Step;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.core.stepexpression.StepTypeRegistry;

public class PickleDefinitionGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PickleDefinitionGenerator.class);

    private Collection<? extends Backend> backends;
    // private Options runnerOptions;
    // private ObjectFactory objectFactory; // not necessary at the moment, I think?

    public PickleDefinitionGenerator(
            Collection<? extends Backend> backends) {
            // ObjectFactory objectFactory) {
        this.backends = backends;
        // this.runnerOptions = runnerOptions;
        // this.objectFactory = objectFactory;
    }

    // do we need to glue? If we need it, we may need to write our own version of
    // the glue
    public String generate(Pickle pickle) {
        // step #1: create step type registry
        StepTypeRegistry stepTypeRegistry = createTypeRegistryForPickle(pickle);
        List<SnippetGenerator> snippetGenerators = createSnippetGeneratorsForPickle(stepTypeRegistry);
        return pickle.getSteps()
                .stream()
                .flatMap(step -> generateSnippetsForStep(step, snippetGenerators).stream())
                .map(SuggestedSnippet::generateString)
                .collect(Collectors.joining("\n\n"));
    }

    private List<SnippetGenerator> createSnippetGeneratorsForPickle(StepTypeRegistry stepTypeRegistry) {
        return backends.stream()
                // we need the backend for the snippet, but tbh, we don't need it!
                .map(Backend::getSnippet)
                .filter(Objects::nonNull)
                .map(snippet -> new SnippetGenerator(snippet, stepTypeRegistry.parameterTypeRegistry()))
                .collect(Collectors.toList());
    }

    private StepTypeRegistry createTypeRegistryForPickle(Pickle pickle) {
        String language = pickle.getLanguage();
        Locale locale = new Locale(language);
        return new StepTypeRegistry(locale);
    }

    private List<SuggestedSnippet> generateSnippetsForStep(Step step, List<SnippetGenerator> snippetGenerators) {
        return snippetGenerators.stream()
                .map(generator -> generator.generate(step))
                .toList();
    }
}
