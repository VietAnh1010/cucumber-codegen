package com.mycompany.app;

import java.util.List;

public class SuggestedPickle {

    // each suggested pickle just contains multiple steps. Nothing else
    private List<SuggestedStep> steps;

    public SuggestedPickle(List<SuggestedStep> steps) {
        this.steps = steps;
    }

    public List<SuggestedStep> getSteps() {
        return steps;
    }
}
