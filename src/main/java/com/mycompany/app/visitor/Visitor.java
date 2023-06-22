package com.mycompany.app.visitor;

import com.mycompany.app.SuggestedFeature;
import com.mycompany.app.SuggestedPickle;
import com.mycompany.app.SuggestedStep;

/**
 * Visit the tree generated by the parser
 */
public interface Visitor<R> {

    public R visit(SuggestedFeature suggestedFeature);

    public R visit(SuggestedPickle suggestedPickle);

    public R visit(SuggestedStep SuggestedStep);
}
