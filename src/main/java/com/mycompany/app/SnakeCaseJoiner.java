package com.mycompany.app;

import java.util.List;
import java.util.stream.Collectors;

public class SnakeCaseJoiner implements Joiner {

    @Override
    public String concatenate(List<String> words) {
        return words.stream()
                .map(String::toLowerCase)
                .collect(Collectors.joining("_"));
    }
}
