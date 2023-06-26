package com.mycompany.app.scanner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;
import com.mycompany.app.logging.Logger;
import com.mycompany.app.logging.LoggerFactory;

import io.cucumber.core.backend.Glue;

/**
 * Scans for JAVA feature files, not CLASS feature files
 * This should be a backend.
 */
public class FeatureScanner {

    // How to scan? How to scan?
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureScanner.class);

    public void loadGlue(Glue glue, List<URI> gluePaths) {
        gluePaths.stream();
    }

    // From the project file, scan all class file and creates the step definition
    // based on the annotations and similar stuffs?
    public static void listClass(File root) {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            // parse the file
            // check the content
            // create step definition based on AST (we interested in the
            // EXISTANCE of the glue, not its content)
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);
                        System.out.println(" * " + n.getName());
                    }
                }.visit(StaticJavaParser.parse(file), null);
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }).explore(root);
        SourceRoot sourceRoot = new SourceRoot(null); // TODO: use this
    }

}