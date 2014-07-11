package com.naivebayesclassifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class UniqueWords {
    private final Map<String, Integer> words;

    public UniqueWords() {
        this.words = new HashMap<>();
    }
    
    public void add(String word){
        int cnt = 0;
        if(words.containsKey(word)){
            cnt = words.get(word) + 1;
        }
        words.put(word, cnt);
    }
    
    public List<String> getNonUniqueWords(List<String> allWords){
        List<String> nonUnique = new ArrayList<>();
        for(String word: allWords){
            if(!words.containsKey(word)){
                nonUnique.add(word);
            }
        }
        return nonUnique;
    }

    public Map<String, Integer> getUniqueWords() {
        return words;
    }
    
}
