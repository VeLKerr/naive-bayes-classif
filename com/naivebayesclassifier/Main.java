package com.naivebayesclassifier;

import com.naivebayesclassifier.curves.Curves;
import com.naivebayesclassifier.dao.ClassifiedMessages;
import com.naivebayesclassifier.dao.GeneralOperations;
import com.naivebayesclassifier.dao.MessageCounts;
import com.naivebayesclassifier.dao.Words;
import com.naivebayesclassifier.reports.ExcelView;
import com.naivebayesclassifier.reports.MetricMatrixes;
import com.naivebayesclassifier.test.TestCurves;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

/**
 * <b><font color="red">Главный класс программы</font></b>
 * @author VeLKerr
 */
public class Main {
    //кол-во папок с сообщениями (в полном наборе данных)
    public static final int PART_NUMBER = getPartNumber();
    
    //путь к папкам с сообщениями (bare в данном наборе можно изметить на
    //lemm, stop или lemm_stop.
    public static final String PATH_TO_FILES = "lingspam_public\\bare\\"; 
    
    //Номер папки набором сообщений, предназначенных для тестировки.
    //По принципу кросс-валидации, обучение на этом наборе проводится не будет.
    //private static int testingDataSetNumber;
    
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
        if (partNumber > 0 && partNumber <= PART_NUMBER) {
            return PATH_TO_FILES + "part" + partNumber + "\\";
        } else {
            System.err.println("Incorrect number of the part!");
            return null;
        }
    }
    
    /**
     * Провести тестирование одного каталога.
     * @param cest объект для рассчёта метрик.
     * @param testingDataSetNumber номер анализируемого каталога.
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException 
     */
    private static void testingOneFolder(ClassificationEstimates cest, int testingDataSetNumber)
            throws ClassNotFoundException, SQLException, IOException{
        File dir = new File(buildPath(testingDataSetNumber));
        cest.addTestingNumber(testingDataSetNumber);
        for(String fname: dir.list()){
            List<String> words = TextPreprocessing.prepareToClassifyFile(dir, fname);
            NaiveBayes nb = new NaiveBayes(words);
            boolean isSpam = nb.isSpam();
            System.err.println("Tested " + fname);
            ClassifiedMessages.add(fname, isSpam);
            Words.addAll(words, nb.getUniqueWords(), isSpam);//после классификации системой сообщения,
            //происходит её дообучение на нём.
        }
    }
    
    /**
     * Режим исследования.
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException 
     */
    private static void testingKFold() throws ClassNotFoundException, SQLException, IOException{
        TextPreprocessing tp = new TextPreprocessing();
        MetricMatrixes mm = new MetricMatrixes(PART_NUMBER, PART_NUMBER);
        ClassificationEstimates cest = new ClassificationEstimates();
        DataSetPartition dsp = new DataSetPartition();
        
        for(int i=1; i<mm.getM(); i++){
            dsp.setTestingSize(i);
            for(int j=1; j<=mm.getK(); j++){
                tp.setLearningDataSetNumbers(dsp.getLearning());
                tp.setTestingDataSetNumbers(dsp.getTesting());
                MessageCounts.createMessageTypes();
                tp.writeToDB();
                for(int number: tp.getTestingNumbers()){
                    testingOneFolder(cest, number);
                }
                mm.setAllMetrics(cest, i, j);
                cest.removeAllTestingNumbers();
                GeneralOperations.deleteAll();
                dsp.nextExperiment();
            }
        }
        ExcelView.generateGeneralReport(mm);
        Curves.create(0, mm.getAverageAccuracy());
        Curves.create(1, mm.getAveragePrecision());
        Curves.create(2, mm.getAverageRecall());
        Curves.create(3, mm.getAverageFMeasure());
    }
    
    /**
     * Вывести текстовое меню.
     * @param mode режим работы системы.
     */
    private static void outputMenu(int mode){
        String str = "Input numbers of folders for ";
        if(mode == 1){
            System.out.println(str + "learning");
        }
        else{
            System.out.println(str + "testing");
        }
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Input mode in which system will works: ");
        int mode = Utils.inputNumber(br, 1, 3);
        switch(mode){
            case 1:{
                outputMenu(mode);
                TextPreprocessing tp = new TextPreprocessing();
                tp.setLearningDataSetNumbers(Utils.inputListNumbers(br, 1, PART_NUMBER));
                tp.writeToDB();
                break;
            }
            case 2:{
                outputMenu(mode);
                TextPreprocessing tp = new TextPreprocessing();
                tp.setTestingDataSetNumbers(Utils.inputListNumbers(br, 1, PART_NUMBER));
                ClassificationEstimates cest = new ClassificationEstimates();
                for(int testingNumber: tp.getTestingNumbers()){
                    testingOneFolder(cest, testingNumber);
                }
                System.out.print("Input BETA for computing F-measures: ");
                double beta = Utils.inputDouble(br, 0.0, Double.MAX_VALUE);
                ExcelView.generateTestingReport(cest, beta);
                break;
            }
            case 3:{
                testingKFold();
                break;
            }
        }
    }
}
