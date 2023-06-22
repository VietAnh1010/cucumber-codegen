package com.mycompany.app;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.cucumber.core.feature.FeatureWithLines;
import io.cucumber.core.resource.ClasspathSupport;

public class MyRuntimeOptions implements io.cucumber.core.feature.Options {

    /**
     * Contains the information needed during the runtime of this application.
     */
    private List<FeatureWithLines> featurePaths = new ArrayList<>();

    public static class Builder {
        // Builder for the runtime
    }

    public void addDefaultFeaturePathIfAbsent() {
        if (featurePaths.isEmpty()) {
            featurePaths.add(FeatureWithLines.create(
                    ClasspathSupport.rootPackageUri(),
                    List.of()));
        }
    }

    @Override
    public List<URI> getFeaturePaths() {
        return featurePaths.stream()
                .map(FeatureWithLines::uri)
                .distinct()
                .sorted()
                .toList();
    }
}
