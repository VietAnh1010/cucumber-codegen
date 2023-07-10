package com.github.vanh1010.cucumber.codegen.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.vanh1010.cucumber.codegen.logging.Logger;
import com.github.vanh1010.cucumber.codegen.logging.LoggerFactory;

public class PropertiesUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);
    private static final String PROPERTIES_FILE_NAME = "cucumber-codegen.properties";

    private PropertiesUtils() {
    }

    public static Map<String, String> fromAllSources() {
        PropertiesMap fromPropertiesFile = new PropertiesMap(fromPropertiesFile());
        Map<String, String> fromEnvironment = fromEnvironment();
        return new PropertiesMap(fromPropertiesFile, fromEnvironment);
    }

    public static Map<String, String> fromPropertiesFile() {
        InputStream inputStream = PropertiesUtils.class.getResourceAsStream("/" + PROPERTIES_FILE_NAME);
        if (inputStream == null) {
            LOGGER.debug(() -> PROPERTIES_FILE_NAME + " does not exist");
            return Collections.emptyMap();
        }
        try (inputStream) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.stringPropertyNames()
                    .stream()
                    .collect(Collectors.toUnmodifiableMap(p -> p, properties::getProperty));
        } catch (IOException ex) {
            LOGGER.warn(ex, () -> PROPERTIES_FILE_NAME + " cannot be loaded");
            return Collections.emptyMap();
        }
    }

    public static Map<String, String> fromEnvironment() {
        return System.getenv();
    }

    private static class PropertiesMap extends AbstractMap<String, String> {

        private final PropertiesMap parent;
        private final Map<String, String> delegate;

        private PropertiesMap(PropertiesMap parent, Map<String, String> delegate) {
            this.parent = parent;
            this.delegate = delegate;
        }

        private PropertiesMap(Map<String, String> delegate) {
            this(null, delegate);
        }

        @Override
        public Set<Entry<String, String>> entrySet() {
            Set<Entry<String, String>> entries = new HashSet<>(delegate.entrySet());
            if (parent != null) {
                entries.addAll(parent.entrySet());
            }
            return entries;
        }

        @Override
        public String get(Object key) {
            String value = delegate.get(key);
            if (value != null) {
                return value;
            }
            if (parent != null) {
                return parent.get(key);
            }
            return null;
        }
    }
}
