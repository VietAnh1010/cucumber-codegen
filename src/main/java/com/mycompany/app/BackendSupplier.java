package com.mycompany.app;

import java.util.function.Supplier;

import io.cucumber.core.backend.Backend;

/**
 * A supplier that is responsible for creating a single backend.
 */
public interface BackendSupplier extends Supplier<Backend> {
}
