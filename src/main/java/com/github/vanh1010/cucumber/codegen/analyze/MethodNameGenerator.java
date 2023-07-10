package com.github.vanh1010.cucumber.codegen.analyze;

import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;

/**
 * Generate a method name and avoid duplication between multiple methods
 */
public class MethodNameGenerator {

    // Note that in Java we can override method
    // But because of generic, it is safer to just generate a new method with
    // different name
    private final CompilationUnit context;
    private final Set<String> methodNames;

    public MethodNameGenerator(CompilationUnit cu) {
        this.context = cu;
        this.methodNames = gatherAllMethodNames();
    }

    // gather ALL method names inside the cu

    public Set<String> gatherAllMethodNames() {
        Set<String> methodNames = new HashSet<>();
        // assume that the file only define A SINGLE TYPE
        TypeDeclaration<?> typeDeclaration = context.getType(0);
        // inside the type declaration there will be mutliple method declaration
        var members = typeDeclaration.getMembers();
        for (var bodyDeclaration : members) {
            if (!bodyDeclaration.isMethodDeclaration()) {
                // we only interested in method declaration
                continue;
            }
            var methodDeclaration = bodyDeclaration.asMethodDeclaration();
            // get the name of the method declaration
            methodNames.add(methodDeclaration.getNameAsString());
        }
        return methodNames;
    }

    public String renameMethod(String methodName) {
        if (!methodNames.contains(methodName)) {
            // no duplication, good!
            return methodName;
        }
        for (int i = 0;; i++) {
            String candidateName = methodName + "_" + i;
            if (!methodNames.contains(candidateName)) {
                return candidateName;
            }
        }
    }
}
