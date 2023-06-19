package com.mycompany.app;

public class SuggestedStep {

    private final String snippet;
    
    public SuggestedStep(String snippet) {
        this.snippet = snippet;
    }

    public String getSnippet() {
        return snippet;
    }

    @Override
    public String toString() {
        return "SuggestedStep [snippet=" + snippet + "]";
    }
}
