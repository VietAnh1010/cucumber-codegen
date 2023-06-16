package com.mycompany.app;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestedFeature {

    private static final String INDENTATION = "    ";
    
    private final String name;
    private final String content;
    private List<SuggestedPickle> pickles;

    public SuggestedFeature(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public List<SuggestedPickle> getPickles() {
        return pickles;
    }

    // TODO: refactor this to another file
    public String generateJava() {
        StringBuilder sb = new StringBuilder();
        sb.append("import io.cucumber.java.en.*;\n\n");
        sb.append("public static class %s {\n\n".formatted(name));
        for (String line : content.split("\n")) {
            // NOTE: the content should be stored as a List of line, or stream of string
            // generate the content lazily
            sb.append(INDENTATION);
            sb.append(line);
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "SuggestedFeature [name=" + name + ", content=" + content + "]";
    }
}
