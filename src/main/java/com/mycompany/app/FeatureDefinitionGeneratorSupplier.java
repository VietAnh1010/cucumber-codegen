package com.mycompany.app;

import java.util.function.Supplier;

@FunctionalInterface
public interface FeatureDefinitionGeneratorSupplier extends Supplier<FeatureDefinitionGenerator> {
}
