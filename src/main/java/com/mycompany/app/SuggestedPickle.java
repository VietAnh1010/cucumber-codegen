package com.mycompany.app;

import java.util.List;

public class SuggestedPickle {

    private final List<SuggestedStep> steps;

    public SuggestedPickle(List<SuggestedStep> steps) {
        this.steps = steps;
    }

    public List<SuggestedStep> getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return "SuggestedPickle [steps=" + steps + "]";
    }
}
