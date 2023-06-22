package com.mycompany.app;

import java.util.List;

public class SuggestedFeature {
    
    private final String name;
    private List<SuggestedPickle> pickles;

    public SuggestedFeature(String name, List<SuggestedPickle> pickles) {
        this.name = name;
        this.pickles = pickles;
    }

    public String getName() {
        return name;
    }

    public List<SuggestedPickle> getPickles() {
        return pickles;
    }

    public boolean hasPickles() {
        return pickles.stream().anyMatch(SuggestedPickle::hasSteps);
    }

    @Override
    public String toString() {
        return "SuggestedFeature [name=" + name + ", pickles=" + pickles + "]";
    }
}
