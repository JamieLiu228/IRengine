package com.trinity.analyzer;

import java.io.IOException;

//同义词提取引擎
public interface SynonymEngine {

	String[] getSynonyms(String s) throws IOException;

}
