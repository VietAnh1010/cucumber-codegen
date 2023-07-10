package com.github.vanh1010.cucumber.codegen.generator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ArgumentPattern(Pattern pattern) {

    public String replaceMatchWith(String name, String replacement) {
        Matcher matcher = pattern.matcher(name);
        String quotedReplacement = Matcher.quoteReplacement(replacement);
        return matcher.replaceAll(quotedReplacement);
    }

    public String replaceMatchesWithSpace(String name) {
        return replaceMatchWith(name, " ");
    }
}
