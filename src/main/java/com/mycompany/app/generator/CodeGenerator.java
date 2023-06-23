package com.mycompany.app.generator;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mycompany.app.SuggestedFeature;
import com.mycompany.app.SuggestedPickle;
import com.mycompany.app.SuggestedStep;

import com.mycompany.app.StreamUtils;

public class CodeGenerator {

    private static final int INDENTATION_SIZE = 4;

    public CodeGenerator() {
    }

    public String generateJavaCode(SuggestedFeature feature) {
        return generateCodeForFeature(feature, 0)
                .map(CodeLine::generate)
                .collect(Collectors.joining("\n"));
    }

    private static int nextIdentation(int identation) {
        return identation + INDENTATION_SIZE;
    }

    private static Stream<CodeLine> stream(JavaLine.WithIdentation builder, String... content) {
        return Arrays.stream(content).map(builder::newLine);
    }

    public Stream<CodeLine> generateCodeForFeature(SuggestedFeature feature, int identation) {
        JavaLine.WithIdentation builder = JavaLine.withIdentation(identation);
        Stream<CodeLine> classDeclaration = stream(
                builder,
                "import io.cucumber.java.en.*;\n",
                "public class %s {".formatted(feature.getName()));
        final int nextIdentation = nextIdentation(identation);
        Stream<CodeLine> classBody = feature.getPickles()
                .stream()
                .flatMap(pickle -> generateCodeForPickle(pickle, nextIdentation));
        Stream<CodeLine> classEnd = stream(builder, "}");
        return StreamUtils.join(classDeclaration, classBody, classEnd);
    }

    public Stream<CodeLine> generateCodeForPickle(SuggestedPickle pickle, int identation) {
        EmptyLine emptyLine = EmptyLine.instance();
        return pickle.getSteps()
                .stream()
                .map(step -> generateCodeForStep(step, identation))
                .reduce((f, s) -> StreamUtils.join(f, Stream.of(emptyLine), s))
                .orElseGet(Stream::of);
    }

    public Stream<CodeLine> generateCodeForStep(SuggestedStep step, int identation) {
        JavaLine.WithIdentation builder = JavaLine.withIdentation(identation);
        return step.getSnippet().lines().map(builder::newLine);
    }
}
