package com.mycompany.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ArgumentPattern {

    private final Pattern pattern;

    public ArgumentPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String replaceMatchWith(String name, String replacement) {
        Matcher matcher = pattern.matcher(name);
        String quotedReplacement = Matcher.quoteReplacement(replacement);
        return matcher.replaceAll(quotedReplacement);
    }

    public String replaceMatchesWithSpace(String name) {
        return replaceMatchWith(name, " ");
    }
}
