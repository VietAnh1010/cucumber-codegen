package com.mycompany.app.generator;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mycompany.app.SuggestedFeature;
import com.mycompany.app.SuggestedPickle;
import com.mycompany.app.SuggestedStep;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.mycompany.app.StreamUtils;

public class CodeGenerator {

    private static final int INDENTATION_SIZE = 4;

    /**
     * Null package name means that there is no package name.
     */
    private final String packageName;

    /**
     * How to inject package name into the code generator?
     */
    public CodeGenerator(String packageName) {
        this.packageName = packageName;
    }

    public CodeGenerator() {
        this(null);
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

    public CompilationUnit newFeatureFile(SuggestedFeature suggestedFeature) {
        CompilationUnit cu = new CompilationUnit();
        if (packageName != null) {
            // add package name
            cu.setPackageDeclaration(packageName);
        }
        cu.addImport(StaticJavaParser.parseImport("import io.cucumber.java.en.*;"));
        ClassOrInterfaceDeclaration cd = cu.addClass(suggestedFeature.getName());
        suggestedFeature.getPickles()
                .stream()
                .flatMap(pickle -> pickle.getSteps().stream())
                .forEach(step -> cd.addMember(parseSnippet(step)));
        return cu;
    }

    public static BodyDeclaration<?> parseSnippet(SuggestedStep suggestedStep) {
        return StaticJavaParser.parseBodyDeclaration(suggestedStep.getSnippet());
    }
}
