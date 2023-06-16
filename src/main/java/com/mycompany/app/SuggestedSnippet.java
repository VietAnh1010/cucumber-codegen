package com.mycompany.app;

import io.cucumber.core.backend.Snippet;

public class SuggestedSnippet {

    private final Snippet snippet;
    private final String keyword;
    private final String pattern;
    private final String functionName;
    private final String arguments;
    private final String instruction;
    private final String tableHint;

    public SuggestedSnippet(Snippet snippet, String keyword, String pattern, 
            String functionName,
            String arguments,
            String instruction,
            String tableHint) {
        this.snippet = snippet;
        this.keyword = keyword;
        this.pattern = pattern;
        this.functionName = functionName;
        this.arguments = arguments;
        // TODO: make this more generic so that we can easily insert new body
        // into the snippet
        // X is the set of vertices that can be added to R to form a bigger clique, but utlimately not because that would
        // cause duplication.
        // Therefore, if X is not empty, it means that the current clique is not a maximal clique
        // but how to explain the pivot element?
        // in this iteration, we will consider only v or its non-neighbors. Why? because the max clique must either contains
        // v or one of its non-neighbor?
        
        this.instruction = instruction;
        this.tableHint = tableHint;
    }

    public String generateString() {
        return snippet.template().format(new String[] {
                keyword,
                pattern,
                functionName,
                arguments,
                instruction,
                tableHint
        });
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getPattern() {
        return pattern;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getArguments() {
        return arguments;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getTableHint() {
        return tableHint;
    }
}
