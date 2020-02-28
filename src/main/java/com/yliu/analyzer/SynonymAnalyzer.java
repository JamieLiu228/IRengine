package com.trinity.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/** 
 * 自定义同义词分词器 
 *  
 * @author Lanxiaowei 
 * @createTime 2015-03-31 10:15:23 
 */  
public class SynonymAnalyzer extends Analyzer {  
  
    private SynonymEngine engine;  
  
    public SynonymAnalyzer(SynonymEngine engine) {  
        this.engine = engine;  
    }  
  
    @SuppressWarnings("deprecation")
	@Override  
    protected TokenStreamComponents createComponents(String text) {  
        Tokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = new SynonymFilter(tokenizer, engine);  
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream,StopAnalyzer.ENGLISH_STOP_WORDS_SET);  
        return new TokenStreamComponents(tokenizer, tokenStream);  
    }  
}  
