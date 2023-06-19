package com.mycompany.app.backend;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import io.cucumber.core.backend.Backend;
import io.cucumber.core.backend.Container;
import io.cucumber.core.backend.Glue;
import io.cucumber.core.backend.Lookup;
import io.cucumber.core.backend.Snippet;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.core.resource.ClasspathScanner;
import io.cucumber.core.resource.ClasspathSupport;

/**
 * This backend only look for step definitions.
 */
public class StepDefinitionBackend implements Backend {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepDefinitionBackend.class);

    /**
     * The look up will be passed into the glue so that it can invoke the methods of the loaded
     * glue.
     * 
     * However, note that we don't need to execute the glue in this case. There, this field is redundant.
     * TODO: remove this field after we can prove that this field is redundant.
     */
    private final Lookup lookup;
    
    /**
     * TODO: prove that the container is necessary or not, and remove if it is redundant.
     */
    private final Container container;

    private final ClasspathScanner classFinder;

    StepDefinitionBackend(Lookup lookup, Container container, Supplier<ClassLoader> classLoaderSupplier) {
        this.lookup = lookup;
        this.container = container;
        this.classFinder = new ClasspathScanner(classLoaderSupplier);
    }

    @Override
    public void loadGlue(Glue glue, List<URI> gluePaths) {
        gluePaths.stream()
                .filter(gluePath -> ClasspathSupport.CLASSPATH_SCHEME.equals(gluePath.getScheme()))
                .map(ClasspathSupport::packageName)
                .map(classFinder::scanForClassesInPackage)
                .flatMap(Collection::stream)
                .distinct()
                .forEach(glueClass -> StepDefinitionScanner.scan(glueClass, (method, annotation) -> {
                    container.addClass(method.getDeclaringClass());
                    // add method and annotation to the pool of defined methods
                    // glueAdaptor.addDefinition(method, annotation);
                    String expression = expression(annotation);
                    glue.addStepDefinition(new TrivalStepDefinition(expression, method));
                }));
    }

    @Override
    public void buildWorld() {
    }

    @Override
    public void disposeWorld() {
    }

    @Override
    public Snippet getSnippet() {
        return new TrivalSnippet();
    }

    private static String expression(Annotation annotation) {
        try {
            Method expressionMethod = annotation.getClass().getMethod("value");
            String expression = (String) Invoker.invoke(annotation, expressionMethod);
            LOGGER.debug(() -> "Got expression: " + expression);
            return expression;
        } catch (NoSuchMethodException e) {
            LOGGER.warn(() -> "Cannot extract expression from annotation");
            throw new IllegalStateException(e);
        }
    }
}
