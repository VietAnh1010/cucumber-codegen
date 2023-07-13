package com.github.vanh1010.cucumber.codegen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import com.github.vanh1010.cucumber.codegen.backend.StepDefinitionBackend;
import com.github.vanh1010.cucumber.codegen.backend.StepDefinitionGlue;
import com.github.vanh1010.cucumber.codegen.generator.CodeLineGenerator;
import com.github.vanh1010.cucumber.codegen.generator.FeatureGenerator;
import com.github.vanh1010.cucumber.codegen.generator.Generator;
import com.github.vanh1010.cucumber.codegen.generator.JavaPoetGenerator;
import com.github.vanh1010.cucumber.codegen.generator.PickleGenerator;
import com.github.vanh1010.cucumber.codegen.generator.StepGenerator;
import com.github.vanh1010.cucumber.codegen.generator.TrivalStepGenerator;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedFeature;
import com.github.vanh1010.cucumber.codegen.writer.DebugFeatureWriter;
import com.github.vanh1010.cucumber.codegen.writer.FileWriter;

import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.core.resource.ClassLoaders;
import io.cucumber.core.runtime.FeaturePathFeatureSupplier;
import io.cucumber.core.runtime.FeatureSupplier;

public class Runtime {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runtime.class);

    private final FeatureSupplier featureSupplier;
    private final FeatureGenerator featureDefinitionGenerator;
    private final FileWriter<SuggestedFeature> fileWriter; // write to file

    private Runtime(
            FeatureSupplier featureSupplier,
            FeatureGenerator featureDefinitionGenerator,
            FileWriter<SuggestedFeature> fileWriter) {
        this.featureSupplier = featureSupplier;
        this.featureDefinitionGenerator = featureDefinitionGenerator;
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
        return featureDefinitionGenerator.generate(feature);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Supplier<ClassLoader> classLoader = null;
        private RuntimeOptions runtimeOptions = null;
        private FeatureSupplier featureSupplier = null;

        private Builder() {
        }

        public Builder runtimeOptions(RuntimeOptions runtimeOptions) {
            this.runtimeOptions = runtimeOptions;
            return this;
        }

        public Builder classLoader(Supplier<ClassLoader> classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Builder featureSupplier(FeatureSupplier featureSupplier) {
            this.featureSupplier = featureSupplier;
            return this;
        }

        private void setRemainingFieldsWithDefault() {
            if (classLoader == null) {
                classLoader = ClassLoaders::getDefaultClassLoader;
            }
            if (runtimeOptions == null) {
                runtimeOptions = RuntimeOptions.defaultOptions();
            }
            if (featureSupplier == null) {
                FeatureParser featureParser = new FeatureParser(UUID::randomUUID);
                featureSupplier = new FeaturePathFeatureSupplier(classLoader, runtimeOptions, featureParser);
            }
        }

        public Runtime build() {
            setRemainingFieldsWithDefault();
            final FeatureSupplier featureSupplier = this.featureSupplier;

            final StepDefinitionBackend stepDefinitionBackend = new StepDefinitionBackend(classLoader);
            final StepDefinitionGlue stepDefinitionGlue = new StepDefinitionGlue();
            stepDefinitionBackend.load(stepDefinitionGlue, runtimeOptions.getGluePaths());

            final StepGenerator stepDefinitionGenerator = new TrivalStepGenerator(runtimeOptions);
            final PickleGenerator pickleDefinitionGenerator = new PickleGenerator(
                    stepDefinitionGenerator,
                    stepDefinitionGlue);
            final FeatureGenerator featureDefinitionGenerator = new FeatureGenerator(
                    pickleDefinitionGenerator);
            final Generator<SuggestedFeature, String> codeGenerator = new JavaPoetGenerator(runtimeOptions);
            // final FileWriter<SuggestedFeature> fileWriter = new
            // DebugFeatureWriter(codeGenerator);
            final FileWriter<SuggestedFeature> fileWriter = new DebugFeatureWriter(codeGenerator);
            return new Runtime(
                    featureSupplier,
                    featureDefinitionGenerator,
                    fileWriter);
        }
    }
}
