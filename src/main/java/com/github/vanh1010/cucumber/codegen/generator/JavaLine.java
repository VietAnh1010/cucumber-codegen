package com.github.vanh1010.cucumber.codegen.generator;

public class JavaLine implements CodeLine {

    private final int identation;
    private final String content;

    private JavaLine(int identation, String content) {
        this.identation = identation;
        this.content = content;
    }

    public static JavaLine of(int identaiton, String content) {
        return new JavaLine(identaiton, content);
    }

    @Override
    public String generate() {
        return " ".repeat(identation) + content;
    }

    public static WithIdentation withIdentation(int identaiton) {
        return new WithIdentation(identaiton);
    }

    public static class WithIdentation {
        private final int identation;

        private WithIdentation(int identation) {
            this.identation = identation;
        }

        public JavaLine newLine(String content) {
            return new JavaLine(identation, content);
        }
    }
}
