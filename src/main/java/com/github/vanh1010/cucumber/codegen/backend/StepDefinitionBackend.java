package com.github.vanh1010.cucumber.codegen.backend;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.core.resource.ClasspathScanner;
import io.cucumber.core.resource.ClasspathSupport;

/**
 * This backend only look for step definitions.
 */
public class StepDefinitionBackend {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepDefinitionBackend.class);

    private final ClasspathScanner classpathScanner;

    public StepDefinitionBackend(Supplier<ClassLoader> classLoaderSupplier) {
        this.classpathScanner = new ClasspathScanner(classLoaderSupplier);
    }

    public void load(StepDefinitionGlue glue, List<URI> gluePaths) {
        gluePaths.stream()
                .filter(gluePath -> ClasspathSupport.CLASSPATH_SCHEME.equals(gluePath.getScheme()))
                .map(ClasspathSupport::packageName)
                .map(classpathScanner::scanForClassesInPackage)
                .flatMap(Collection::stream)
                .distinct()
                .forEach(clazz -> StepDefinitionScanner.scan(clazz, (method, annotation) -> {
                    String expression = expression(annotation);
                    glue.addStepDefinition(new TrivalStepDefinition(expression, method));
                    // TODO: handle other stuffs
                }));
    }

    private static String expression(Annotation annotation) {
        try {
            Method expressionMethod = annotation.getClass().getMethod("value");
            String expression = (String) Invoker.invoke(annotation, expressionMethod);
            LOGGER.debug(() -> "Got expression: " + expression);
            return expression;
        } catch (NoSuchMethodException ex) {
            LOGGER.warn(() -> "Cannot extract expression from annotation");
            throw new BackendException("Cannot extract expression from annotation", ex);
        }
    }
}
