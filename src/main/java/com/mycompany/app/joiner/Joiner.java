package com.mycompany.app.joiner;

import java.util.List;

public interface Joiner {

    public static String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public String concatenate(List<String> words);
}
