package com.github.vanh1010.cucumber.codegen.generator;

import java.util.List;

import javax.lang.model.element.Modifier;

import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedAnnotation;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedFeature;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedParameter;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedPickle;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedStep;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class JavaPoetGenerator implements Generator<SuggestedFeature, String> {

    private final Options options;

    public JavaPoetGenerator(Options options) {
        this.options = options;
    }

    @Override
    public String generate(SuggestedFeature feature) {
        var typeSpec = fromFeature(feature);
        JavaFile file = JavaFile.builder(options.getPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .indent(" ".repeat(options.getIndentation()))
                .build();
        return file.toString();
    }

    public TypeSpec fromFeature(SuggestedFeature feature) {
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(feature.name());
        feature.pickles()
                .stream()
                .map(this::fromPickle)
                .flatMap(List::stream)
                .forEach(typeSpecBuilder::addMethod);
        return typeSpecBuilder.build();

    }

    public List<MethodSpec> fromPickle(SuggestedPickle pickle) {
        return pickle.steps()
                .stream()
                .map(this::fromStep)
                .toList();
    }

    public MethodSpec fromStep(SuggestedStep step) {
        List<ParameterSpec> parameters = step.parameters()
                .stream()
                .map(this::fromParameter)
                .toList();
        AnnotationSpec annotation = fromAnnotation(step.annotation());
        return MethodSpec
                .methodBuilder(step.name())
                .addAnnotation(annotation)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameters(parameters)
                .addCode(step.implementation())
                .build();
    }

    public AnnotationSpec fromAnnotation(SuggestedAnnotation annotation) {
        return AnnotationSpec
                .builder(annotation.annotation())
                .addMember("value", "$S", annotation.pattern())
                .build();
    }

    public ParameterSpec fromParameter(SuggestedParameter parameter) {
        return ParameterSpec
                .builder(parameter.type(), parameter.name())
                .build();
    }
}
