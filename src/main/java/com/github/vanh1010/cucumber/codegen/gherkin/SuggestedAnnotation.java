package com.github.vanh1010.cucumber.codegen.gherkin;

import java.lang.annotation.Annotation;

public record SuggestedAnnotation(Class<? extends Annotation> annotation, String pattern) {
}
