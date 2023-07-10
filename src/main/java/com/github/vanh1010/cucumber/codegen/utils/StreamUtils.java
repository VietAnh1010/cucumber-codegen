package com.github.vanh1010.cucumber.codegen.utils;

import java.util.Arrays;
import java.util.stream.Stream;

public class StreamUtils {

    private StreamUtils() {
    }

    @SafeVarargs
    public static <T> Stream<T> join(Stream<? extends T>... streams) {
        @SuppressWarnings("unchecked")
        Stream<T>[] streamsT = (Stream<T>[]) streams;
        return Arrays.stream(streamsT)
                .reduce(Stream::concat)
                .orElseGet(Stream::empty);
    }
}
