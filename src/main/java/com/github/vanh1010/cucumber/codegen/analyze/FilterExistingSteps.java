package com.github.vanh1010.cucumber.codegen.analyze;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedFeature;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedParameter;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedPickle;
import com.github.vanh1010.cucumber.codegen.gherkin.SuggestedStep;
import com.github.vanh1010.cucumber.codegen.utils.SuggestedGherkinUtils;

/**
 * Read a compilation unit
 */
public class FilterExistingSteps {

    public CompilationUnit integrate(CompilationUnit cu, SuggestedFeature feature) {
        // we need to look at all import statement inside the compilation unit
        // whenever we generate a new method, we need to look at the required import and
        // merge that with this import
        // I don't know whether this already handle duplicate imports for us
        extractImports(feature).forEach(cu::addImport);
        feature = renameMethodName(cu, feature);

        return cu;
    }

    public Set<String> extractImports(SuggestedFeature feature) {
        // TODO: handle annotation type
        // we should know the annotation type even before we try to generate the source
        // code
        Set<String> imports = new HashSet<>();
        ImportResolver resolver = new ImportResolver();
        feature.pickles().stream().map(SuggestedPickle::steps).flatMap(List::stream)
                .flatMap(step -> step.parameters().stream().map(SuggestedParameter::type))
                .forEach(type -> resolver.extractImport(imports, type));
        return imports;
    }

    // we need another function that helps us to rename all methods to avoid
    // collision
    // before we can append it to the file
    // to be more precise, we need to remap the method name
    // I wish we have lense in this case TT

    // TODO: only change the structure when there is changes in the substructure,
    // otherwise
    // we will reuse the same instance
    public SuggestedFeature renameMethodName(CompilationUnit cu, SuggestedFeature feature) {
        MethodNameGenerator generator = new MethodNameGenerator(cu);
        return new SuggestedFeature(feature.name(),
                feature.pickles().stream().map(pickle -> renameMethodName(generator, pickle)).toList());
    }

    public SuggestedPickle renameMethodName(MethodNameGenerator generator, SuggestedPickle pickle) {
        return new SuggestedPickle(pickle.steps().stream().map(step -> renameMethodName(generator, step)).toList());
    }

    public SuggestedStep renameMethodName(MethodNameGenerator generator, SuggestedStep step) {
        String stepName = step.name();
        String newStepName = generator.renameMethod(stepName);
        if (stepName.equals(newStepName)) {
            return step;
        }
        return new SuggestedStep(step.annotation(), newStepName, step.parameters(), step.implementation());
    }

    /**
     * Integrates the suggested feature into the compilation unit.
     */
    public void integrateMethod(CompilationUnit cu, SuggestedFeature feature) {
        var typeDeclarations = cu.getTypes();
        ClassOrInterfaceDeclaration declaration = switch (typeDeclarations.size()) {
            case 0 ->
                new ClassOrInterfaceDeclaration(NodeList.nodeList(Modifier.publicModifier()), false, feature.name());
            case 1 -> typeDeclarations.get(0).toClassOrInterfaceDeclaration().orElseThrow(
                    () -> new RuntimeException("The declaration inside the step definition file is not a class"));
            default -> throw new RuntimeException("Too many type defined in a step definition file");
        };

        MethodDeclarationGenerator generator = new MethodDeclarationGenerator();

        // now we have the declaration, we can starts to add the method into the class
        SuggestedGherkinUtils.streamOfSteps(feature).forEach(step -> {
            MethodDeclaration methodDeclaration = generator.generate();
            declaration.addMember(methodDeclaration);
        });
    }
}
