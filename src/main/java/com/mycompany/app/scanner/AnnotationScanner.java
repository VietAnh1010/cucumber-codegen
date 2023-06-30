package com.mycompany.app.scanner;

import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.mycompany.app.backend.StepDefinitionGlue;

public class AnnotationScanner {
    

    public static void main(String[] args) {
        CompilationUnit cu = StaticJavaParser.parse(
            """
            public class Foo {

                @Given(\"foo\")
                public void foo(int i, int j) {

                }

                @When(\"bar\")
                public void bar(List<String> blah) {
                    
                }
            }
            """
        );
        System.out.println(cu);
        AnnotationExpr expr = cu.getTypes().get(0).getMembers().get(0).getAnnotations().get(0);
        if (expr instanceof SingleMemberAnnotationExpr smExpr) {
            String name = expr.getNameAsString(); // given when then?
            Expression content = smExpr.getMemberValue();
            if (content instanceof StringLiteralExpr stringContent) {
                // this will be the value of our annotation
                // now we need to apply this code to all annotation inside the
                // folder

                stringContent.getValue();
            } else {
                throw new RuntimeException("Exptect String in the annotation");
            }
        } else {
            throw new RuntimeException("Syntax error on the annotation");
        }
        cu.accept(new MethodScanner(), new StepDefinitionGlue());
    }

    public List<SingleMemberAnnotationExpr> getStepsAnnotation(CompilationUnit cu) {
        List<? extends AnnotationExpr> list = cu.getTypes().stream()
                .flatMap(type -> type.getMembers().stream())
                .flatMap(member -> member.getAnnotations().stream())
                .filter(annotation -> annotation instanceof SingleMemberAnnotationExpr)
                .toList();
        /**
         * We know 100% that this is safe
         */
        @SuppressWarnings("unchecked")
        List<SingleMemberAnnotationExpr> annotations = (List<SingleMemberAnnotationExpr>) list;
        return annotations;
    }

    /**
     * Used to scan all methods inside a compilation unit.
     */
    public List<MethodDeclaration> getMethods(CompilationUnit cu) {
        List<? extends BodyDeclaration<?>> list = cu.getTypes().stream()
                .flatMap(type -> type.getMembers().stream())
                .filter(member -> member instanceof MethodDeclaration)
                .toList();
        @SuppressWarnings("unchecked")
        List<MethodDeclaration> methods = (List<MethodDeclaration>) list;
        return methods;
    }

    public List<SingleMemberAnnotationExpr> getAnnotationsFromMethod(MethodDeclaration method) {
        List<? extends AnnotationExpr> list = method.getAnnotations()
                .stream()
                .filter(annotation -> annotation instanceof SingleMemberAnnotationExpr)
                .toList();
        /**
         * We know 100% that this is safe
         */
        @SuppressWarnings("unchecked")
        List<SingleMemberAnnotationExpr> annotations = (List<SingleMemberAnnotationExpr>) list;
        return annotations;
    }
    
    /**
     * For each step definition file
     * We use Static Java Parser to create an AST for the file
     * Then we run the scanner over the file
     * And finally we collect the step definitions
     * Note that we can also use visitor to handle the Method Declaration inside a file
     */

}
