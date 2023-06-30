package com.mycompany.app.scanner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.mycompany.app.backend.TrivalStepDefinition;
import com.mycompany.app.logging.Logger;
import com.mycompany.app.logging.LoggerFactory;

import io.cucumber.core.backend.Glue;

/**
 * Do we care abt the context? If yes, we may pass to context into the visit
 * function
 * If we do so, we need to change the signature of this class in the future
 */
public class MethodScanner extends VoidVisitorAdapter<Glue> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodScanner.class);
    private static final Set<String> KEYWORDS = Set.of("Given", "When", "Then");

    @Override
    public void visit(MethodDeclaration method, Glue glue) {
        LOGGER.info(() -> "Handling method declaration: " + method);
        List<String> parameterNames = method.getParameters()
                .stream()
                .map(Parameter::getTypeAsString)
                .map(MethodScanner::removeTypeParameter)
                .toList();
        List<Type> parameterTypes = new ArrayList<>();
        for (String name : parameterNames) {
            Class<?> parameterClass = ClassFinder.findWithSimpleName(name);
            if (parameterClass == null) {
                LOGGER.info(() -> "Unknown parameter class: " + name);
                return;
            }
            parameterTypes.add(parameterClass);
        }
        Type[] parameters = parameterTypes.toArray(Type[]::new);
        method.getAnnotations()
                .stream()
                .forEach(annotation -> handle(glue, parameters, annotation));       
    }

    private static String removeTypeParameter(String typeName) {
        return typeName.replaceAll("<.*>", "");
    }

    private static void handle(Glue glue, Type[] parameters, AnnotationExpr annotation) {
        if (!(annotation instanceof SingleMemberAnnotationExpr singleMemberAnnotation)) {
            return;
        }
        if (!KEYWORDS.contains(singleMemberAnnotation.getNameAsString())) {
            return;
        }
        Expression expr = singleMemberAnnotation.getMemberValue();
        if (!(expr instanceof StringLiteralExpr stringExpr)) {
            return;
        }
        String expression = stringExpr.getValue();
        LOGGER.info(() -> "Expression: " + expression);
        glue.addStepDefinition(new TrivalStepDefinition(expression, parameters));
    }
}
