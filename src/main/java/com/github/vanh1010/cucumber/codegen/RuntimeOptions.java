package com.github.vanh1010.cucumber.codegen;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.github.vanh1010.cucumber.codegen.utils.AnnotationUtils;

import io.cucumber.core.feature.FeatureWithLines;
import io.cucumber.core.resource.ClasspathSupport;

/**
 * Contains the information needed during the runtime of this application.
 */
public class RuntimeOptions implements
        io.cucumber.core.feature.Options,
        com.github.vanh1010.cucumber.codegen.backend.Options,
        com.github.vanh1010.cucumber.codegen.generator.Options,
        com.github.vanh1010.cucumber.codegen.writer.Options {

    private final List<FeatureWithLines> featurePaths;
    private final List<URI> gluePaths;
    private final String packageName;
    private final Path outputDir;
    private final Set<Class<? extends Annotation>> annotations;

    private RuntimeOptions(
            List<FeatureWithLines> featurePaths,
            List<URI> gluePaths,
            String packageName,
            Path outputDir,
            Set<Class<? extends Annotation>> annotations) {
        this.featurePaths = featurePaths;
        this.gluePaths = gluePaths;
        this.packageName = packageName;
        this.outputDir = outputDir;
        this.annotations = annotations;
    }

    @Override
    public List<URI> getFeaturePaths() {
        return featurePaths.stream()
                .map(FeatureWithLines::uri)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public Path getOutputDir() {
        return outputDir;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public List<URI> getGluePaths() {
        return Collections.unmodifiableList(gluePaths);
    }

    @Override
    public Set<Class<? extends Annotation>> getAnnotations() {
        return annotations;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static RuntimeOptions defaultOptions() {
        return builder()
                .addDefaultAnnotations()
                .addDefaultFeaturePathIfAbsent()
                .addDefaultGluePathIfAbsent()
                .build();
    }

    public static class Builder {

        private List<FeatureWithLines> featurePaths = new ArrayList<>();
        private List<URI> gluePaths = new ArrayList<>();
        private String packageName = "";
        private Path outputDir = Path.of("").toAbsolutePath();
        private Set<Class<? extends Annotation>> annotations = new HashSet<>();

        private Builder() {
        }

        public Builder addFeaturePath(FeatureWithLines featurePath) {
            featurePaths.add(featurePath);
            return this;
        }

        public Builder addGluePath(URI gluePath) {
            gluePaths.add(gluePath);
            return this;
        }

        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder outputDir(Path outputDir) {
            this.outputDir = outputDir;
            return this;
        }

        public Builder addAnnotation(Class<? extends Annotation> annotation) {
            annotations.add(annotation);
            return this;
        }

        public RuntimeOptions build() {
            return new RuntimeOptions(
                    featurePaths,
                    gluePaths,
                    packageName,
                    outputDir,
                    annotations);
        }

        public Builder addDefaultFeaturePathIfAbsent() {
            if (featurePaths.isEmpty()) {
                FeatureWithLines featureWithLines = FeatureWithLines.create(
                        ClasspathSupport.rootPackageUri(),
                        List.of());
                featurePaths.add(featureWithLines);
            }
            return this;
        }

        public Builder addDefaultGluePathIfAbsent() {
            if (gluePaths.isEmpty()) {
                gluePaths.add(ClasspathSupport.rootPackageUri());
            }
            return this;
        }

        public Builder addDefaultAnnotations() {
            Stream<String> defaultAnnotations = Stream.of(
                    "io.cucumber.java.en.Given",
                    "io.cucumber.java.en.Then",
                    "io.cucumber.java.en.When");
            defaultAnnotations.map(AnnotationUtils::find).forEach(annotations::add);
            return this;
        }
    }
}
