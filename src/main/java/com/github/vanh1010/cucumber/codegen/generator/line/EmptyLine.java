package com.github.vanh1010.cucumber.codegen.generator.line;

public class EmptyLine implements CodeLine {

    private static final EmptyLine INSTANCE = new EmptyLine();

    private EmptyLine() {
    }

    public static EmptyLine instance() {
        return INSTANCE;
    }

    @Override
    public String generate() {
        return "";
    }

    @Override
    public String toString() {
        return "EmptyLine []";
    }
}