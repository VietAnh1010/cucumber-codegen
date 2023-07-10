package com.github.vanh1010.cucumber.codegen.generator.joiner;

import java.util.List;
import java.util.stream.Collectors;

public class PascalCaseJoiner implements Joiner {

    @Override
    public String concatenate(List<String> words) {
        return words.stream()
                .map(Joiner::capitalize)
                .collect(Collectors.joining());
    }
}
