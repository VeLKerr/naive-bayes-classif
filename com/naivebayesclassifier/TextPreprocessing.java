package com.naivebayesclassifier;

import com.naivebayesclassifier.dao.Words;
import com.naivebayesclassifier.dao.MessageCounts;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс, отвечающий за предобработку текста, разбиение его на слова, а также
 * занесение этих слов в базу.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class TextPreprocessing {
    /**
     * Номера папок с тестировочным набором сообщений.
     */
    private List<Integer> testingDataSetNumbers;
    /**
     * Номера папок обучающей выборки.
     */
    private List<Integer> learningDataSetNumbers;

    public TextPreprocessing() {
        this.testingDataSetNumbers = new ArrayList<>();
        this.learningDataSetNumbers = new ArrayList<>();
    }
    
    public void addTestingNumber(int number){
        testingDataSetNumbers.add(number);
    }
    
    public void addLearningNumber(int number){
        learningDataSetNumbers.add(number);
    }
    
    public List<Integer> getLearningNumbers(){
        return learningDataSetNumbers;
    }
    
    public List<Integer> getTestingNumbers(){
        return testingDataSetNumbers;
    }
    
    /**
     * Запись тренировочной выборки в БД.
     * @throws IOException при ошибке чтения текста из файла с сообщением.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера.
     * @throws SQLException при ошибке выполнения запроса.
     */
    public void writeToDB() throws IOException, SQLException, ClassNotFoundException{
        for(int lNumber: learningDataSetNumbers){
            writeToDB(lNumber);
        }
    }
    
    /**
     * Запись тренировочной выборки в БД.
     * @param learningDataSetNumber номер каталога с сообщениями для обучения.
     * @throws IOException при ошибке чтения текста из файла с сообщением.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера.
     * @throws SQLException при ошибке выполнения запроса.
     */
    public void writeToDB(int learningDataSetNumber) throws IOException, SQLException, ClassNotFoundException{
        if(learningDataSetNumbers.contains(learningDataSetNumber)){
            File dir = new File(Main.buildPath(learningDataSetNumber));
            for(String fname: dir.list()){
                boolean isSpam = fname.contains(Main.SPAM_FILE_FEATURE);
                MessageCounts.incrementCounter(isSpam);
                try {
                    BufferedReader br = new BufferedReader(new FileReader(new File(dir, fname)));
                    String line = null;
                    while((line = br.readLine()) != null){
                        toDB(preprocess(line), isSpam);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(TextPreprocessing.class.getName()).log(Level.SEVERE, "File not found!", ex);
                }
                System.err.println("Trained on " + fname);
            }
        }
    }
    
    /**
     * Предобработка файла сообщения.
     * @param parent путь к файлу.
     * @param fileName название файла.
     * @return список слов, получившихся после разбиения.
     * @throws IOException при ошибке чтения текста из файла с сообщением.
     */
    public static List<String> prepareToClassifyFile(File parent, String fileName) throws IOException{
        List<String> words = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(parent, fileName)));
            String line = null;
                while((line = br.readLine()) != null){
                    words.addAll(preprocess(line));
                }
        }
        catch(FileNotFoundException ex){
            Logger.getLogger(TextPreprocessing.class.getName()).log(Level.SEVERE, "File not found!", ex);
        }
        return words;
    }
    
    /**
     * Предобработка текста. Проводится в два этапа:
     * <ul>
     *  <li>замена всех заглавных букв прописными;</li>
     *  <li>разбиение такста сообщения на слова.</li>
     * </ul>
     * @param text текст сообщения.
     * @return список слов.
     */
    private static List<String> preprocess(String text){
        List<String> words = new ArrayList<>();
        text = text.toLowerCase(Locale.US);
        Pattern r = Pattern.compile("[A-z]+[a-zA-Z0-9]*");
        Matcher m = r.matcher(text);
        while(m.find()){
            String word = m.group();
            if(!word.equals("[") && !word.equals("]") && !word.equals("-")){
                words.add(word);
            }
        }
        return words;
    }
    
    /**
     * Запись тренировочного сообщения, разбитого на слова в БД.
     * @param words список слов.
     * @param isSpam флажок, устанавливающий, к каому классу принадлежит
     * данное сообщение.
     */
    private void toDB(List<String> words, boolean isSpam){
        for(String w: words){
            try{
                Words.add(w, isSpam);
            }
            catch(ClassNotFoundException | SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public void setTestingDataSetNumbers(List<Integer> testingDataSetNumbers) {
        this.testingDataSetNumbers = testingDataSetNumbers;
    }

    public void setLearningDataSetNumbers(List<Integer> learningDataSetNumbers) {
        this.learningDataSetNumbers = learningDataSetNumbers;
    }
    
}
