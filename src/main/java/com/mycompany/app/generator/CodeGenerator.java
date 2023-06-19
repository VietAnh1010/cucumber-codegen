package com.mycompany.app.generator;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mycompany.app.SuggestedFeature;
import com.mycompany.app.SuggestedPickle;
import com.mycompany.app.SuggestedStep;

public class CodeGenerator {

    // Instead of returning a string, we should return a stream of strings.
    // This allow us to deffer the actual computation
    // And allow us to modify/iden the code if necessary
    public static String addIdentation(String line) {
        return " ".repeat(4) + line;
    }

    public CodeGenerator() {
    }

    public String generateJavaCode(SuggestedFeature feature) {
        return generateCodeForFeature(feature).collect(Collectors.joining("\n"));
    }

    public Stream<String> generateCodeForFeature(SuggestedFeature feature) {
        Stream<String> classDeclaration = Stream.of("""
                public static class %s {
                """.formatted(feature.getName()));

        Stream<String> classBody = feature.getPickles()
                .stream()
                .flatMap(this::generateCodeForPickle)
                .map(CodeGenerator::addIdentation);

        Stream<String> classEnd = Stream.of("}");
        return Stream.of(classDeclaration, classBody, classEnd)
                .reduce(Stream::concat)
                .get();
    }

    public Stream<String> generateCodeForPickle(SuggestedPickle pickle) {
        return pickle.getSteps()
                .stream()
                .flatMap(this::generateCodeForStep);
    }

    public Stream<String> generateCodeForStep(SuggestedStep step) {
        return step.getSnippet().lines();
    }
}
