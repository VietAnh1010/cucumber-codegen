package com.github.vanh1010.cucumber.codegen.generator.line;

public record JavaLine(int indentation, String content) implements CodeLine {

    @Override
    public String generate() {
        return " ".repeat(indentation) + content;
    }

    public static WithIndentation withIdentation(int identaiton) {
        return new WithIndentation(identaiton);
    }

    public static record WithIndentation(int indentation) {

        public JavaLine newLine(String content) {
            return new JavaLine(indentation, content);
        }
    }
}
