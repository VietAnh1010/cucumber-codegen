package com.github.vanh1010.cucumber.codegen.generator;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * General API
 */
public interface Options {

    String getPackageName();

    Set<Class<? extends Annotation>> getAnnotations();
}
