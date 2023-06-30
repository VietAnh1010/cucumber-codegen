package com.mycompany.app.generator;

import java.util.Collection;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.mycompany.app.SuggestedFeature;
import com.mycompany.app.SuggestedPickle;
import com.mycompany.app.SuggestedStep;

public class ASTCodeGenerator implements Generator<SuggestedFeature, String> {
    
    private final String packageName;

    public ASTCodeGenerator(String packageName) {
        this.packageName = packageName;
    }

    public ASTCodeGenerator() {
        this(null);
    }

    public static BodyDeclaration<?> parseSnippet(SuggestedStep suggestedStep) {
        return StaticJavaParser.parseBodyDeclaration(suggestedStep.getSnippet());
    }

    @Override
    public String generate(SuggestedFeature feature) {
        return null;
    }

    public CompilationUnit buildCompilationUnit(SuggestedFeature feature) {
        CompilationUnit cu = new CompilationUnit();
        if (packageName != null && !packageName.isBlank()) {
            cu.setPackageDeclaration(packageName);
        }
        cu.addImport(StaticJavaParser.parseImport("import io.cucumber.java.en.*;"));
        ClassOrInterfaceDeclaration cd = cu.addClass(feature.getName());
        feature.getPickles()
                .stream()
                .map(SuggestedPickle::getSteps)
                .flatMap(Collection::stream)
                .map(ASTCodeGenerator::parseSnippet)
                .forEach(cd::addMember);
        return cu;
    }
}
