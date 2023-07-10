package com.github.vanh1010.cucumber.codegen.backend;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.java.StepDefinitionAnnotation;
import io.cucumber.java.StepDefinitionAnnotations;

/**
 * Scan step definitions in a class.
 */
public class StepDefinitionScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepDefinitionScanner.class);

    private StepDefinitionScanner() {
    }

    public static void scan(Class<?> clazz, BiConsumer<Method, Annotation> consumer) {
        if (Object.class.equals(clazz)) {
            return;
        }
        Method[] clazzMethods = new Method[0];
        try {
            clazzMethods = clazz.getMethods();
        } catch (NoClassDefFoundError ex) {
            LOGGER.warn(() -> "Cannot load methods of class " + clazz.getName());
        }
        for (Method method : clazzMethods) {
            handle(clazz, method, consumer);
        }
    }

    public static void handle(
            Class<?> clazz,
            Method method,
            BiConsumer<Method, Annotation> consumer) {
        if (Object.class.equals(method.getDeclaringClass()) || method.isBridge()) {
            return;
        }
        for (Annotation annotation : method.getAnnotations()) {
            if (isStepDefinitionAnnotation(annotation)) {
                consumer.accept(method, annotation);
            } else if (isRepeatedStepDefinitionAnnotation(annotation)) {
                // a single annotation that contains multiple "sub" annotations
                // recursively traverse all "expanded" annotation
            }
        }
    }

    private static boolean isStepDefinitionAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        return annotationClass.getAnnotation(StepDefinitionAnnotation.class) != null;
    }

    private static boolean isRepeatedStepDefinitionAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        return annotationClass.getAnnotation(StepDefinitionAnnotations.class) != null;
    }
}
