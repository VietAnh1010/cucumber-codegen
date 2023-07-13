package com.github.vanh1010.cucumber.codegen.generator;

import java.util.Collection;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.vanh1010.cucumber.codegen.analyze.ImportResolver;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedAnnotation;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedFeature;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedParameter;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedPickle;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedStep;

// TODO: continue to fix this
public class ASTCodeGenerator implements Generator<SuggestedFeature, CompilationUnit> {

    private final Options options;
    private CompilationUnit compilationUnit = null;

    public ASTCodeGenerator(Options options) {
        this.options = options;
    }

    /**
     * Prepares the context required to run this generator.
     * <p>
     * For this generator, the context is a {@link CompilationUnit}. The generator
     * will then accepts an input, and modifies the context with the given input.
     * 
     * @param compilationUnit the context
     */
    public void prepareContext(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    public MethodDeclaration step(SuggestedStep step) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        methodDeclaration.setName(step.name());
        methodDeclaration.setAnnotations(NodeList.nodeList(annotation(step.annotation())));
        // we need to set the parameter as well
        methodDeclaration.setBody(StaticJavaParser.parseBlock(step.implementation()));
        return methodDeclaration;
    }

    public SingleMemberAnnotationExpr annotation(SuggestedAnnotation annotation) {
        SingleMemberAnnotationExpr annotationExpr = new SingleMemberAnnotationExpr();
        StringLiteralExpr patternExpr = new StringLiteralExpr(annotation.pattern());
        annotationExpr.setName(annotation.annotation().getName());
        annotationExpr.setMemberValue(patternExpr);
        return annotationExpr;
    }

    public Parameter parameter(SuggestedParameter parameter) {
        return null;
    }

    @Override
    public CompilationUnit generate(SuggestedFeature feature) {
        return null;
    }

    public CompilationUnit buildCompilationUnit(SuggestedFeature feature) {
        // TODO: fix
        CompilationUnit cu = new CompilationUnit();
        String packageName = options.getPackageName();
        if (packageName != null && !packageName.isBlank()) {
            cu.setPackageDeclaration(packageName);
        }
        cu.addImport(StaticJavaParser.parseImport("import io.cucumber.java.en.*;"));
        ClassOrInterfaceDeclaration cd = cu.addClass(feature.name());
        feature.pickles()
                .stream()
                .map(SuggestedPickle::steps)
                .flatMap(Collection::stream);
        return cu;
    }

    // from the context, we need to somehow extract the necessary information?
    {
        ImportResolver importResolver = null;
    }
}
