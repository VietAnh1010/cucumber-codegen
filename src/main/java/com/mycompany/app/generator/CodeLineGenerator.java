package com.mycompany.app.generator;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mycompany.app.StreamUtils;
import com.mycompany.app.SuggestedFeature;
import com.mycompany.app.SuggestedPickle;
import com.mycompany.app.SuggestedStep;

public class CodeLineGenerator implements Generator<SuggestedFeature, String> {

    private static final int INDENTATION_SIZE = 4;

    /**
     * Null package name means that there is no package name.
     */
    private final String packageName;

    /**
     * How to inject package name into the code generator?
     */
    public CodeLineGenerator(String packageName) {
        this.packageName = packageName;
    }

    public CodeLineGenerator() {
        this(null);
    }

    private static int nextIdentation(int identation) {
        return identation + INDENTATION_SIZE;
    }

    @Override
    public String generate(SuggestedFeature feature) {
        return generateCodeForFeature(feature, 0)
                .map(CodeLine::generate)
                .collect(Collectors.joining("\n"));
    }

    public Stream<CodeLine> generateCodeForFeature(SuggestedFeature feature, int identation) {
        JavaLine.WithIdentation builder = JavaLine.withIdentation(identation);

        String classDeclarationString = "public class " + feature.getName() + " {";
        Stream<CodeLine> classDeclaration = Stream.of(
                builder.newLine("import io.cucumber.java.en.*;"),
                EmptyLine.instance(),
                builder.newLine(classDeclarationString));

        if (packageName != null && !packageName.isBlank()) {
            classDeclaration = Stream.concat(
                    Stream.of(builder.newLine("package " + packageName + ";")),
                    classDeclaration);
        }

        int nextIdentation = nextIdentation(identation);
        Stream<CodeLine> classBody = feature.getPickles()
                .stream()
                .flatMap(pickle -> generateCodeForPickle(pickle, nextIdentation));
        Stream<CodeLine> classEnd = Stream.of(builder.newLine("}"));
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
