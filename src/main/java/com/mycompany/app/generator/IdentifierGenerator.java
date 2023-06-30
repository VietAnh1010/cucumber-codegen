package com.mycompany.app.generator;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.mycompany.app.joiner.Joiner;

public class IdentifierGenerator {

    private static final String BETWEEN_LOWER_AND_UPPER = "(?<=\\p{Ll})(?=\\p{Lu})";
    private static final String BEFORE_UPPER_AND_LOWER = "(?<=\\p{L})(?=\\p{Lu}\\p{Ll})";
    private static final Pattern SPLIT_CAMEL_CASE = Pattern
            .compile(BETWEEN_LOWER_AND_UPPER + "|" + BEFORE_UPPER_AND_LOWER);
    private static final Pattern SPLIT_WHITESPACE = Pattern.compile("\\s");
    private static final Pattern SPLIT_UNDERSCORE = Pattern.compile("_");
    private static final char SPACE = ' ';

    private final Joiner joiner;

    public IdentifierGenerator(Joiner joiner) {
        this.joiner = joiner;
    }

    public String generate(String sentence) {
        if (sentence.isEmpty()) {
            throw new IllegalArgumentException("Cannot create function name from empty sentence");
        }
        List<String> words = Stream.of(sentence)
                .map(IdentifierGenerator::replaceIllegalCharacters)
                .map(String::strip)
                .flatMap(SPLIT_WHITESPACE::splitAsStream)
                .flatMap(SPLIT_CAMEL_CASE::splitAsStream)
                .flatMap(SPLIT_UNDERSCORE::splitAsStream)
                .toList();
        return joiner.concatenate(words);
    }

    private static String replaceIllegalCharacters(String sentence) {
        StringBuilder sanitized = new StringBuilder();
        char start = sentence.charAt(0);
        sanitized.append(Character.isJavaIdentifierStart(start) ? start : SPACE);
        for (int i = 1; i < sentence.length(); i++) {
            if (Character.isJavaIdentifierPart(sentence.charAt(i))) {
                sanitized.append(sentence.charAt(i));
            } else if (sanitized.charAt(sanitized.length() - 1) != SPACE && i != sentence.length() - 1) {
                sanitized.append(SPACE);
            }
        }
        return sanitized.toString();
    }
}
