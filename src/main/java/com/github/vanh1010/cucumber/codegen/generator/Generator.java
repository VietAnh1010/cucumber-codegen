package com.github.vanh1010.cucumber.codegen.generator;

public interface Generator<In, Out> {

    public Out generate(In input);
}
