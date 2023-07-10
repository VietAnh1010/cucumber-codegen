package com.github.vanh1010.cucumber.codegen.utils;

import java.lang.annotation.Annotation;

import com.github.vanh1010.cucumber.codegen.logging.Logger;
import com.github.vanh1010.cucumber.codegen.logging.LoggerFactory;

public class AnnotationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationUtils.class);

    private AnnotationUtils() {
    }

    public static Class<? extends Annotation> find(String name) {
        try {
            Class<?> clazz = Class.forName(name);
            if (!clazz.isAnnotation()) {
                throw new ClassNotFoundException(name + " is not annotation");
            }
            @SuppressWarnings("unchecked")
            var annotation = (Class<? extends Annotation>) clazz;
            return annotation;
        } catch (ClassNotFoundException ex) {
            LOGGER.warn(ex, () -> "Cannot find annotation %s, use default value".formatted(name));
            throw new RuntimeException(ex);
        }
    }
}
