package com.trinity.analyzer;

import java.io.IOException;
import java.util.HashMap;

public class SimpleSynonymEngine implements SynonymEngine {

    private static HashMap<String, String[]> map = new HashMap<>();

    {
        map.put("quick", new String[]{"fast", "speedy"});
        map.put("jumps", new String[]{"leaps", "hops"});
        map.put("over", new String[]{"above"});
        map.put("lazy", new String[]{"apathetic", "slugish"});
        map.put("dog", new String[]{"canine", "pooch"});
    }

    @Override
    public String[] getSynonyms(String s) throws IOException {
        return map.get(s);
    }

}
