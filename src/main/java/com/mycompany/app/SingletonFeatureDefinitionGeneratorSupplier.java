package com.mycompany.app;

import java.util.Collection;

import io.cucumber.core.backend.Backend;
import io.cucumber.core.backend.ObjectFactory;
import io.cucumber.core.runner.Options;
import io.cucumber.core.runtime.BackendSupplier;
import io.cucumber.core.runtime.ObjectFactorySupplier;

public class SingletonFeatureDefinitionGeneratorSupplier implements FeatureDefinitionGeneratorSupplier {

    private final BackendSupplier backendSupplier;
    private final ObjectFactorySupplier objectFactorySupplier;
    private final Options options;
    private FeatureDefinitionGenerator generator;
    
    public SingletonFeatureDefinitionGeneratorSupplier(
                BackendSupplier backendSupplier, 
                ObjectFactorySupplier objectFactorySupplier,
                Options options
    ) {
        this.backendSupplier = backendSupplier;
        this.objectFactorySupplier = objectFactorySupplier;
        this.options = options;
    }

    public FeatureDefinitionGenerator get() {
        if (generator == null) {
            Collection<? extends Backend> backends = backendSupplier.get();
            ObjectFactory objectFactory = objectFactorySupplier.get();
            generator = new FeatureDefinitionGenerator(backends, options, objectFactory);
        }
        return generator;
    }
}
