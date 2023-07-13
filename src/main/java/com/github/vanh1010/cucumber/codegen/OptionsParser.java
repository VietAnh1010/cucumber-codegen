package com.github.vanh1010.cucumber.codegen;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.vanh1010.cucumber.codegen.logging.Logger;
import com.github.vanh1010.cucumber.codegen.logging.LoggerFactory;
import com.github.vanh1010.cucumber.codegen.utils.AnnotationUtils;

import io.cucumber.core.feature.FeatureWithLines;
import io.cucumber.core.feature.GluePath;

/**
 * Parses options from a source. For now, we will support properties file and
 * environment only.
 */
public class OptionsParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionsParser.class);

    private static final String PROPERTY_PREFIX = "cucumber.codegen";

    private static final String GLUES_PROPERTY_NAME = property("glues");
    private static final String FEATURES_PROPERTY_NAME = property("features");
    private static final String ANNOTATIONS_PROPERTY_NAME = property("annotations");
    private static final String OUTPUT_DIR_PROPERTY_NAME = property("outputDir");
    private static final String INDENTATION_PROPERTY_NAME = property("indentation");
    private static final String PACKAGE_NAME_PROPERTY_NAME = property("packageName");

    private static String property(String name) {
        return PROPERTY_PREFIX + name;
    }

    private final RuntimeOptions.Builder builder;
    private final Map<String, String> properties;

    public OptionsParser(RuntimeOptions.Builder builder, Map<String, String> properties) {
        this.builder = builder;
        this.properties = properties;
    }

    public void parse() {
        parseMany(GLUES_PROPERTY_NAME,
                splitThenApply(GluePath::parse),
                builder::addGluePath);

        parseMany(FEATURES_PROPERTY_NAME,
                splitThenApply(FeatureWithLines::parse),
                builder::addFeaturePath);

        parseMany(ANNOTATIONS_PROPERTY_NAME,
                splitThenApply(AnnotationUtils::find),
                builder::addAnnotation);

        parse(OUTPUT_DIR_PROPERTY_NAME, Path::of, builder::outputDir);
        parse(INDENTATION_PROPERTY_NAME, Integer::parseInt, builder::indentation);
        parse(PACKAGE_NAME_PROPERTY_NAME, Function.identity(), builder::packageName);
    }

    private <T> void parse(
            String name,
            Function<String, T> parser,
            Consumer<T> action) {
        String str = properties.get(name);
        if (str == null) {
            return;
        }
        try {
            T value = parser.apply(str);
            action.accept(value);
        } catch (Exception ex) {
            LOGGER.warn(ex, () -> "Cannot parse property %s with value %s".formatted(name, str));
        }
    }

    private <T> void parseMany(
            String name,
            Function<String, List<T>> parser,
            Consumer<T> action) {
        String str = properties.get(name);
        if (str == null) {
            return;
        }
        try {
            List<T> values = parser.apply(str);
            values.forEach(action);
        } catch (Exception ex) {
            LOGGER.warn(ex, () -> "Cannot parse property %s with value %s".formatted(name, str));
        }
    }

    private static <T> Function<String, List<T>> splitThenApply(Function<String, T> parser) {
        return str -> Arrays.stream(str.split(","))
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .map(parser)
                .toList();
    }
}
