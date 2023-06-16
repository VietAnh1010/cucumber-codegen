package com.mycompany.app;

/**
 * Instances of this interface will be used to generate the implementation for
 * each step definition
 * <p>
 * The easiest implementation for this interface is a trivial generator that
 * always generate a throw statement
 */
public interface ImplementationGenerator {

    public String generate();
}
