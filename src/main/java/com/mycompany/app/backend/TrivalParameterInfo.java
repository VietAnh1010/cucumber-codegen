package com.mycompany.app.backend;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import io.cucumber.core.backend.ParameterInfo;
import io.cucumber.core.backend.TypeResolver;

public class TrivalParameterInfo implements ParameterInfo {

    private final Type type;

    public TrivalParameterInfo(Type type) {
        this.type = type;
    }

    public static List<ParameterInfo> fromMethod(Method method) {
        return Arrays.stream(method.getGenericParameterTypes())
                .<ParameterInfo>map(TrivalParameterInfo::new)
                .toList();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean isTransposed() {
        throw new UnsupportedOperationException("Data table is not supported at the moment");
    }

    @Override
    public TypeResolver getTypeResolver() {
        throw new UnsupportedOperationException("Data table and doc string is not supported at the moment");
    }
}
