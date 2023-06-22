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

    public boolean hasSteps() {
        return !steps.isEmpty();
    }

    @Override
    public String toString() {
        return "SuggestedPickle [steps=" + steps + "]";
    }
}
