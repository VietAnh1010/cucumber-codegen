package com.mycompany.app.backend;

import java.util.Map;
import java.util.stream.Collectors;
import java.lang.reflect.Type;
import java.text.MessageFormat;

import io.cucumber.core.backend.Snippet;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;

public class TrivalSnippet implements Snippet {

    @Override
    public final String tableHint() {
        return """
                // For automatic transformation, change DataTable to one of
                // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
                // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
                // Double, Byte, Short, Long, BigInteger or BigDecimal.
                //
                // For other transformations you can register a DataTableType.
                """;
    }

    @Override
    public final String arguments(Map<String, Type> arguments) {
        return arguments.entrySet()
                .stream()
                .map(argType -> getArgType(argType.getValue()) + " " + argType.getKey())
                .collect(Collectors.joining(", "));
    }

    private String getArgType(Type argType) {
        if (argType instanceof Class) {
            Class<?> cType = (Class<?>) argType;
            if (cType.equals(DataTable.class)) {
                return cType.getName();
            }
            return cType.getSimpleName();
        }
        return argType.toString();
    }

    @Override
    public MessageFormat template() {
        return new MessageFormat("""
                @{0}("{1}")
                public void {2}({3}) '{'
                    // {4}
                {5}    throw new """ + PendingException.class.getName() + "();\n" +
                "'}'");
    }

    @Override
    public String escapePattern(String pattern) {
        return pattern.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
