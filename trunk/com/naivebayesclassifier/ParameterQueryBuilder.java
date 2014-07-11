package com.naivebayesclassifier;

import java.util.Map.Entry;

/**
 * Класс для построения строк запросов
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class ParameterQueryBuilder {
    private static final String INSERTION_STARTING = "insert into words values (?, ";
    
    private static abstract class InsertionAll{
        private static final String GENERAL_STARTING = "Insert All ";
        private static final String GENERAL_ENDING = "Select Null From Dual";
        private static final String STARTING = "into words values (\'";
        private static final String MIDDLE_PART = "\', ";
        private static final String ENDING = ") ";
    }
    
    private static abstract class Counting{
        private static final String STARTING = "Select Sum(";
        private static final String ENDING = "amcnt) From Words";
    }
    
    private static abstract class Updating{
        private static final String STARTING = "update words set ";
        private static final String MIDDLE_PART = "amcnt = ";
        private static final String ENDING = "amcnt + 1 where text = ?";
    }
    
//    private static abstract class Updating{
//        private static final String STARTING = "update words\n" +
//            "set ";
//        private static final String MIDDLE_PART = "amcnt = 1 + (\n" +
//            "  select ";
//        private static final String ENDING = "amcnt\n" +
//            "  from words\n" +
//            "  where text like ?\n" +
//            ")\n" +
//            "where text like ?";
//    }
    
    /**
     * Если <code>true</code>, построение запросов будет производится для
     * класса SPAM. Иначе - для HAM.
     */
    private final boolean isSpam;
    
    public ParameterQueryBuilder(boolean isSpam) {
        this.isSpam = isSpam;
    }
    
    /**
     * Построить запрос на вставку новго слова в таблицу Words.
     * @return строка запроса.
     */
    public String buildInsert(){
        if(isSpam){
            return INSERTION_STARTING + "1, 0)";
        }
        return INSERTION_STARTING + "0, 1)";
    }
    
    /**
     * Построить запрос на обновление данных об уже существующем слове в
     * таблице Words.
     * @return строка запроса.
     */
    public String buildUpdate(){
        String addition = "h";
        if(isSpam){
            addition = "sp";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Updating.STARTING).append(addition).append(Updating.MIDDLE_PART);
        sb.append(addition).append(Updating.ENDING);
        return sb.toString();
    }
    
    /**
     * Построение запроса на подсчёт общего кол-ва слов из спамовых
     * или неспамовых писем.
     * @return строка запроса.
     */
    public String buildCount(){
        String addition = "h";
        if(isSpam){
            addition = "sp";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Counting.STARTING).append(addition).append(Counting.ENDING);
        return sb.toString();
    }
    
    public String buildInsertAll(UniqueWords uniqueWords){
        StringBuilder sb = new StringBuilder();
        sb.append(InsertionAll.GENERAL_STARTING);
        for(Entry<String, Integer> entry: uniqueWords.getUniqueWords().entrySet()){
            sb.append(InsertionAll.STARTING).append(entry.getKey()).append(InsertionAll.MIDDLE_PART);
            if(isSpam){
                sb.append(entry.getValue()).append(", 0");
            }
            else{
                sb.append("0, ").append(entry.getValue());
            }
            sb.append(InsertionAll.ENDING);
        }
        sb.append(InsertionAll.GENERAL_ENDING);
        return sb.toString();
    }
}
