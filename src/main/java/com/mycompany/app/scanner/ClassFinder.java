package com.mycompany.app.scanner;

import java.util.Map;

public class ClassFinder {

    /*
     * We may hard code some classes, and let the program scan for other classes if necessary.
     */
    private static final String PACKAGES[] = {
        "java.lang",
        "java.util",
        "java.math"
    };

    private static final Map<String, Class<?>> PRIMITIVE_CLASSES = Map.of(
        "long", long.class,
        "int", int.class,
        "short", short.class,
        "byte", byte.class,
        "char", char.class,
        "boolean", boolean.class,
        "double", double.class,
        "float", float.class
    );

    public static Class<?> findWithSimpleName(String name) {
        Class<?> primitiveClass = PRIMITIVE_CLASSES.get(name);
        if (primitiveClass != null) {
            return primitiveClass;
        }
        for (String packageName : PACKAGES) {
            String fullName = String.join(".", packageName, name);
            try {
                Class<?> refClass = Class.forName(fullName);
                return refClass;
            } catch (ClassNotFoundException ex) {
            }
        }
        return null;
    }
}
