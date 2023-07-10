package com.github.vanh1010.cucumber.codegen.generator;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.vanh1010.cucumber.codegen.generator.line.CodeLine;
import com.github.vanh1010.cucumber.codegen.generator.line.EmptyLine;
import com.github.vanh1010.cucumber.codegen.generator.line.JavaLine;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedAnnotation;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedFeature;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedPickle;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedStep;
import com.github.vanh1010.cucumber.codegen.utils.StreamUtils;

public class CodeLineGenerator implements Generator<SuggestedFeature, String> {

    private static final int INDENTATION_SIZE = 4;

    private final Options options;

    public CodeLineGenerator(Options options) {
        this.options = options;
    }

    private static int nextIndentation(int indentation) {
        return indentation + INDENTATION_SIZE;
    }

    @Override
    public String generate(SuggestedFeature feature) {
        return generateCodeForFeature(feature, 0)
                .map(CodeLine::generate)
                .collect(Collectors.joining("\n"));
    }

    public Stream<CodeLine> generateCodeForFeature(SuggestedFeature feature, int indentation) {
        JavaLine.WithIndentation builder = JavaLine.withIdentation(indentation);
        String classDeclarationString = "public class %s {".formatted(feature.name());
        Stream<CodeLine> classDeclaration = Stream.of(builder.newLine(classDeclarationString));
        String packageName = options.getPackageName();
        if (!packageName.isBlank()) {
            String packageDeclarationString = "package %s;".formatted(packageName);
            classDeclaration = Stream.concat(
                    Stream.of(builder.newLine(packageDeclarationString), EmptyLine.instance()),
                    classDeclaration);
        }
        int nextIndentation = nextIndentation(indentation);
        Stream<CodeLine> classBody = feature.pickles()
                .stream()
                .flatMap(pickle -> generateCodeForPickle(pickle, nextIndentation));
        Stream<CodeLine> classEnd = Stream.of(builder.newLine("}"));
        return StreamUtils.join(classDeclaration, classBody, classEnd);
    }

    public Stream<CodeLine> generateCodeForPickle(SuggestedPickle pickle, int indentation) {
        EmptyLine emptyLine = EmptyLine.instance();
        return pickle.steps()
                .stream()
                .map(step -> generateCodeForStep(step, indentation))
                .reduce((f, s) -> StreamUtils.join(f, Stream.of(emptyLine), s))
                .orElseGet(Stream::of);
    }

    public Stream<CodeLine> generateCodeForStep(SuggestedStep step, int indentation) {
        JavaLine.WithIndentation signature = JavaLine.withIdentation(indentation);
        JavaLine.WithIndentation implementation = JavaLine.withIdentation(nextIndentation(indentation));
        SuggestedAnnotation annotation = step.annotation();
        String annotationUsage = "@%s(\"%s\")".formatted(
                annotation.annotation().getName(),
                annotation.pattern());
        String parametersString = step.parameters()
                .stream()
                .map(parameter -> "%s %s".formatted(nameOf(parameter.type()), parameter.name()))
                .collect(Collectors.joining(", "));
        String methodDeclarationString = "public void %s(%s) {".formatted(
                step.name(),
                parametersString);
        Stream<CodeLine> methodDeclaration = Stream.of(
                signature.newLine(annotationUsage),
                signature.newLine(methodDeclarationString));
        Stream<CodeLine> methodBody = Arrays
                .stream(step.implementation().split("\n"))
                .map(implementation::newLine);
        Stream<CodeLine> methodEnd = Stream.of(signature.newLine("}"));
        return StreamUtils.join(methodDeclaration, methodBody, methodEnd);
    }

    private static String nameOf(Type type) {
        String typeName = type.getTypeName();
        if (typeName.startsWith("java.lang.")) {
            typeName = typeName.substring(10);
        }
        return typeName;
    }
}
