package com.naivebayesclassifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import com.naivebayesclassifier.dao.ClassifiedMessages;
import com.naivebayesclassifier.dao.GeneralOperations;
import com.naivebayesclassifier.dao.MessageCounts;
import com.naivebayesclassifier.dao.Words;

/**
 * <b><font color="red">Главный класс программы</font></b>
 * @author VeLKerr
 */
public class Main {
    //кол-во папок с сообщениями
    public static final int PART_NUMBER = getPartNumber(); 
    //путь к папкам с сообщениями
    public static final String PATH_TO_FILES = "lingspam_public\\bare\\"; 
    
    //Номер папки набором сообщений, предназначенных для тестировки.
    //По принципу кросс-валидации, обучение на этом наборе проводится не будет.
    private static int testingDataSetNumber;
    
    //строка, которая встречается токлько в названиях спам-сообщений.
    //Используется для экспертной классификации сообщений, т.е. для 
    //классификации без участия НБК.
    public static final String SPAM_FILE_FEATURE = "spmsg";
    
    /**
     * Получить кол-во папок с сообщениями.
     * @return кол-во папок с сообщениями
     */
    private static int getPartNumber(){
        return new File(PATH_TO_FILES).list().length;
    }
    
    /**
     * Построить относительный путь к сообщениям.
     * @param partNumber номер подпапки.
     * @return строка относительного пути.
     */
    public static String buildPath(int partNumber) {
        if (partNumber > 0 && partNumber < 11) {
            return PATH_TO_FILES + "part" + partNumber + "\\";
        } else {
            System.err.println("Incorrect number of the part!");
            return null;
        }
    }
    
    /**
     * Ввести число.
     * @param min минимальная граница вводимых чисел.
     * @param max максимальная граница.
     * @return полученное число.
     * @throws IOException 
     */
    private static int inputNumber(int min, int max)throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int number = 0;
        while(number == 0){
            try{
                number = Integer.parseInt(br.readLine());
                if(number < min || number > max){
                    throw new NumberFormatException();
                }
            }
            catch(NumberFormatException nfe){
                number = 0;
                System.out.print("\tIncorrect! Input once more: ");
            }
        }
        return number;
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        TextPreprocessing tp = null;
        System.out.println("If you want that the system was trained, press 1.");
        System.out.println("If you want that the system classified messages, press 2.");
        int mode = inputNumber(1, 2);
        System.out.print("Input number of folder which remains for classification: ");
        testingDataSetNumber = inputNumber(1, PART_NUMBER);
        tp = new TextPreprocessing(testingDataSetNumber); 
        if(mode == 1){
            GeneralOperations.deleteAll(); //удаление данных прошлого сеанса обучения.
            //(это делается для того, чтобы исключить ошибку нарушения уникальности слов в базе.
            //Такая ошибка возможна т.к. обучение происходит на тех же 10-и папках набора bare).
            MessageCounts.createMessageTypes();
            tp.writeToDB();
        }
        else{
            File dir = new File(buildPath(testingDataSetNumber));
            ClassificationEstimates cest = new ClassificationEstimates(testingDataSetNumber);
            for(String fname: dir.list()){
                List<String> words = tp.prepareToClassifyFile(dir, fname);
                NaiveBayes nb = new NaiveBayes(words);
                boolean isSpam = nb.isSpam();
                System.out.println(isSpam);
                ClassifiedMessages.add(fname, isSpam);
                Words.addAll(words, isSpam); //после классификации системой сообщения,
                //происходит её дообучение на нём.
            }
            System.out.println("=========================================");
            System.out.println("Accuracy: " + cest.computeAccuracy());
            List<Double> estimates = cest.computeEstimates();
            System.out.println("\nPrecision(spam): " + estimates.get(0));
            System.out.println("Recall(spam): " + estimates.get(1));
            System.out.println("Precision(ham): " + estimates.get(2));
            System.out.println("Recall(ham): " + estimates.get(3));
            List<Double> measures = cest.computeFMeasure(estimates, 1);
            System.out.println("\nF-measure(spam): " + measures.get(0));
            System.out.println("\nF-measure(ham): " + measures.get(1));
            System.out.println("=========================================");
        }
    }
}
