package com.mycompany.app;

import java.util.function.Function;
import java.util.stream.Stream;

// I am not sure whether this is neccesary or not
public class LineStream {

    private final Stream<String> lines;

    public LineStream(Stream<String> lines) {
        this.lines = lines;
    }

    public LineStream map(Function<String, String> mapper) {
        return null;
    }
}
