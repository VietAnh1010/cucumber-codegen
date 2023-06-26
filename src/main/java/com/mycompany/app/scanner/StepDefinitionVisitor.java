package com.mycompany.app.scanner;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class StepDefinitionVisitor extends VoidVisitorAdapter<Object> {
    

    @Override
    public void visit(final MethodDeclaration node, final Object ignore) {
        // should gather the method and the annotation and add the created expression
        // into the glue
        var annotations = node.getAnnotations();
        var name = node.getName().getIdentifier();

        // use Class::forName to get the annotation class object.
        // we can then cast it to annotation and check whether the annotations is appropriate
    }

    // TODO: fix this
    public Class<?> retrieveClassFromString(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException cex) {
            return null;
        }
    }
}
