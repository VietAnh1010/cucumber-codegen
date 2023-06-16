package com.mycompany.app;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.cucumber.core.eventbus.EventBus;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.filter.Filters;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.core.gherkin.Pickle;
import io.cucumber.core.gherkin.Step;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.order.PickleOrder;
import io.cucumber.core.plugin.PluginFactory;
import io.cucumber.core.plugin.Plugins;
import io.cucumber.core.resource.ClassLoaders;
import io.cucumber.core.runtime.BackendServiceLoader;
import io.cucumber.core.runtime.BackendSupplier;
import io.cucumber.core.runtime.CucumberExecutionContext;
import io.cucumber.core.runtime.ExitStatus;
import io.cucumber.core.runtime.FeaturePathFeatureSupplier;
import io.cucumber.core.runtime.FeatureSupplier;
import io.cucumber.core.runtime.ObjectFactoryServiceLoader;
import io.cucumber.core.runtime.ObjectFactorySupplier;
import io.cucumber.core.runtime.SingletonObjectFactorySupplier;
import io.cucumber.core.runtime.ThreadLocalObjectFactorySupplier;
import io.cucumber.core.runtime.TimeServiceEventBus;
import io.cucumber.core.runtime.UuidGeneratorServiceLoader;

public class Runtime {
    private static final Logger LOGGER = LoggerFactory.getLogger(Runtime.class);

    private final FeatureSupplier featureSupplier;
    private final FeatureDefinitionGeneratorSupplier featureGeneratorSupplier;
    private final CodeGenerator codeGenerator;
    private final FileWriter<SuggestedFeature> fileWriter; // write to file

    private Runtime(
            FeatureSupplier featureSupplier,
            FeatureDefinitionGeneratorSupplier featureGeneratorSupplier,
            CodeGenerator codeGenerator,
            FileWriter<SuggestedFeature> fileWriter) {
        this.featureSupplier = featureSupplier;
        this.featureGeneratorSupplier = featureGeneratorSupplier;
        this.codeGenerator = codeGenerator;
        this.fileWriter = fileWriter;
    }

    public void run() {
        try {
            List<Feature> features = featureSupplier.get();
            suggestFeatures(features);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.warn(() -> "Syntax error?");
        }
    }

    private void suggestFeatures(List<Feature> features) {
        List<SuggestedFeature> suggestedFeatures = new ArrayList<>();
        for (Feature feature : features) {
            LOGGER.info(() -> "Handle feature @ " + feature.getUri());
            SuggestedFeature suggestedFeature = suggestFeature(feature);
            suggestedFeatures.add(suggestedFeature);
        }
        fileWriter.write(suggestedFeatures);
    }

    private SuggestedFeature suggestFeature(Feature feature) {
        FeatureDefinitionGenerator generator = featureGeneratorSupplier.get();
        return generator.generate(feature);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Supplier<ClassLoader> classLoader = ClassLoaders::getDefaultClassLoader;
        private RuntimeOptions runtimeOptions = RuntimeOptions.defaultOptions();
        private BackendSupplier backendSupplier;
        private FeatureSupplier featureSupplier;

        private Builder() {
        }

        public Builder withRuntimeOptions(final RuntimeOptions runtimeOptions) {
            this.runtimeOptions = runtimeOptions;
            return this;
        }

        public Builder withClassLoader(final Supplier<ClassLoader> classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Builder withBackendSupplier(final BackendSupplier backendSupplier) {
            this.backendSupplier = backendSupplier;
            return this;
        }

        public Builder withFeatureSupplier(final FeatureSupplier featureSupplier) {
            this.featureSupplier = featureSupplier;
            return this;
        }

        public Runtime build() {
            final ObjectFactoryServiceLoader objectFactoryServiceLoader = new ObjectFactoryServiceLoader(
                    classLoader,
                    runtimeOptions);

            final ObjectFactorySupplier objectFactorySupplier = runtimeOptions.isMultiThreaded()
                    ? new ThreadLocalObjectFactorySupplier(objectFactoryServiceLoader)
                    : new SingletonObjectFactorySupplier(objectFactoryServiceLoader);

            final BackendSupplier backendSupplier = this.backendSupplier != null
                    ? this.backendSupplier
                    : new BackendServiceLoader(this.classLoader, objectFactorySupplier);

            final FeatureParser parser = new FeatureParser(UUID::randomUUID);
            final FeatureSupplier featureSupplier = this.featureSupplier != null
                    ? this.featureSupplier
                    : new FeaturePathFeatureSupplier(classLoader, runtimeOptions, parser);

            final FeatureDefinitionGeneratorSupplier featureDefinitionGeneratorSupplier = new SingletonFeatureDefinitionGeneratorSupplier(
                    backendSupplier, objectFactorySupplier, runtimeOptions);

            final CodeGenerator codeGenerator = new CodeGenerator();

            // final FileWriter<SuggestedFeature> fileWriter = new DebugFeatureWriter();
            final FileWriter<SuggestedFeature> fileWriter = new BufferedFeatureWriter(codeGenerator);
            return new Runtime(
                    featureSupplier,
                    featureDefinitionGeneratorSupplier,
                    codeGenerator,
                    fileWriter);
        }
    }
}
