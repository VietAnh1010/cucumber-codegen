package com.mycompany.app.joiner;

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
                functionName.append(Joiner.capitalize(word));
            }
        }
        return functionName.toString();
    }
}
