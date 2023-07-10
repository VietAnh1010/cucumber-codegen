package com.github.vanh1010.cucumber.codegen.analyze;

import java.lang.String;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

public class ImportResolver {

    private static final String IMPLICITLY_IMPORTED_PACKAGE = "java.lang";

    public void extractImport(Set<String> imports, Type type) {
        if (type instanceof Class<?> clazz) {
            String clazzName = clazz.getName();
            if (!clazzName.contains(".") || clazzName.startsWith(IMPLICITLY_IMPORTED_PACKAGE)) {
                return;
            }
            imports.add(clazzName);
        } else if (type instanceof ParameterizedType parameterizedType) {
            extractImport(imports, parameterizedType.getRawType());
            for (Type typeArg : parameterizedType.getActualTypeArguments()) {
                extractImport(imports, typeArg);
            }
        }
    }
}
