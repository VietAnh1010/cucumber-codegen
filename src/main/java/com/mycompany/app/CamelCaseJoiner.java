package com.mycompany.app;

import java.util.List;

public class CamelCaseJoiner implements Joiner {

    public String concatenate(List<String> words) {
        StringBuilder functionName = new StringBuilder();
        boolean firstWord = true;
        for (String word : words) {
            if (firstWord) {
                functionName.append(word.toLowerCase());
                firstWord = false;
            } else {
                functionName.append(capitalize(word));
            }
        }
        return functionName.toString();
    }

    private static String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
