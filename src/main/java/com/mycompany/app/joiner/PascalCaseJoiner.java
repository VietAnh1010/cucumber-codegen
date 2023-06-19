package com.mycompany.app.joiner;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Instances of this class represents a joiner that joins words together using
 * Pascal Case.
 * <p>
 * This joiner will be used to generate class names.
 */
public class PascalCaseJoiner implements Joiner {

    @Override
    public String concatenate(List<String> words) {
        return words.stream()
                .map(Joiner::capitalize)
                .collect(Collectors.joining());
    }
}
