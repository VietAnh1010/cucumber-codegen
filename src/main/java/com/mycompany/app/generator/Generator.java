package com.mycompany.app.generator;

public interface Generator<In, Out> {

    public Out generate(In input);
}
