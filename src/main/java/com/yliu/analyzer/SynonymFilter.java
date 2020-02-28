package com.trinity.analyzer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.util.Stack;

/**
 * 自定义同义词过滤器
 *
 * @author Lanxiaowei
 */
public class SynonymFilter extends TokenFilter {

    private final CharTermAttribute termAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private Stack<String> synonymStack;
    private SynonymEngine engine;
    private State current;

    public SynonymFilter(TokenStream in, SynonymEngine engine) {
        super(in);
        synonymStack = new Stack<>();
        this.engine = engine;

        this.termAtt = addAttribute(CharTermAttribute.class);
        this.posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (synonymStack.size() > 0) {
            String syn = synonymStack.pop();
            restoreState(current);

            termAtt.copyBuffer(syn.toCharArray(), 0, syn.length());
            posIncrAtt.setPositionIncrement(0);
            return true;
        }

        if (!input.incrementToken()){
            return false;
        }

        if (addAliasesToStack()) {
            current = captureState();
        }

        return true;
    }

    private boolean addAliasesToStack() throws IOException {
        String[] synonyms = engine.getSynonyms(termAtt.toString());

        if (synonyms == null) {
            return false;
        }
        for (String synonym : synonyms) {
            synonymStack.push(synonym);
        }
        return true;
    }
}  