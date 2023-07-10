package com.github.vanh1010.cucumber.codegen.generator;

import java.util.Collection;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedFeature;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedPickle;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedStep;

public class ASTCodeGenerator implements Generator<SuggestedFeature, String> {

    private final String packageName;

    public ASTCodeGenerator(String packageName) {
        this.packageName = packageName;
    }

    public ASTCodeGenerator() {
        this(null);
    }

    public static BodyDeclaration<?> parseSnippet(SuggestedStep suggestedStep) {
        return StaticJavaParser.parseBodyDeclaration("");
    }

    @Override
    public String generate(SuggestedFeature feature) {
        return null;
    }

    public CompilationUnit buildCompilationUnit(SuggestedFeature feature) {
        // TODO: fix
        CompilationUnit cu = new CompilationUnit();
        if (packageName != null && !packageName.isBlank()) {
            cu.setPackageDeclaration(packageName);
        }
        cu.addImport(StaticJavaParser.parseImport("import io.cucumber.java.en.*;"));
        ClassOrInterfaceDeclaration cd = cu.addClass(feature.name());
        feature.pickles()
                .stream()
                .map(SuggestedPickle::steps)
                .flatMap(Collection::stream)
                .map(ASTCodeGenerator::parseSnippet)
                .forEach(cd::addMember);
        return cu;
    }
}
