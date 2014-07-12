package com.naivebayesclassifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <font color="green">Класс, описывающий слова сообщения, которые являются
 * новыми для обучающей выборки</font>.
 * <p>При дообучении системы на уже классифицированном сообщении вносятся такие
 * изменения в таблицу <code>Words</code>. Если слово в таблице не встречалось, 
 * создаём новую запись. Если же такое слово уже существует, инкрементируем
 * соответствующий счётчик.</p>
 * <p> Проверка слова на уникальность забирает машинное время, так же как и
 * выполнение серии запросов типа INSERT. В то время, как при тестировке проверка
 * слов на уникальность уже косвенно осуществлялась.</p>
 * <p> Чтобы ускорить процес дообучения, предпочтительнее собирать все новые слова
 * в специальную коллекцию, чтобы потом для них всех выполнить INSERT ALL, а для
 * тех, которые в коллекцию не попали - UPDATE.</p>
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class UniqueWords {
    /**
     * Мапа уникальных слов.
     */
    private final Map<String, Integer> words;

    public UniqueWords() {
        this.words = new HashMap<>();
    }
    
    /**
     * Добавить слово в коллекцию.
     * @param word новое слово.
     */
    public void add(String word){
        int cnt = 0;
        if(words.containsKey(word)){
            cnt = words.get(word) + 1;
        }
        words.put(word, cnt);
    }
    
    /**
     * Получить неуникальные слова. Т.е. те, которые в базе обучающей выборки
     * уже существуют.
     * @param allWords все слова анализируемого сообщения.
     * @return список неуникальных слов.
     */
    public List<String> getNonUniqueWords(List<String> allWords){
        List<String> nonUnique = new ArrayList<>();
        for(String word: allWords){
            if(!words.containsKey(word)){
                nonUnique.add(word);
            }
        }
        return nonUnique;
    }
    
    /**
     * Получить уникальные слова.
     * @return мапа уникальных слов:
     * <ul>
     * <li>ключ (<code> String </code>) - слово;</li>
     * <li>значение (<code> Integer </code>) - сколько раз это слово
     * встречалось в анализируемом сообщении.</li>
     * </ul>
     */
    public Map<String, Integer> getUniqueWords() {
        return words;
    }
    
}
