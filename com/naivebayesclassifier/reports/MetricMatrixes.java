package com.naivebayesclassifier.reports;

import com.naivebayesclassifier.ClassificationEstimates;
import com.naivebayesclassifier.Utils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для хранения матриц с метриками. Будет использоваться для построения Learning Curves.
 * Каждая матрица имеет размерность 9*9, при том заполняется только её часть над побочной
 * диагональю.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class MetricMatrixes {
    private static final int BETA = 1;
    /**
     * Матрица точностей.
     */
    private final double[][] accuracy;
    /**
     * Матрица прецизионностей для классов SPAM и HAM.
     */
    private final List<double[][]> precision = new ArrayList<>();
    /**
     * Матрица полноты для классов SPAM и HAM.
     */
    private final List<double[][]> recall = new ArrayList<>();
    /**
     * Матрица F-мер для классов SPAM и HAM.
     */
    private final List<double[][]> fMeasure = new ArrayList<>();
    
    /**
     * Инициализировать матрицы с метриками.
     * @param m кол-во каталогов в наборе данных.
     * @param k кол-во эксперементов в K-fold кроссвалидации.
     */
    public MetricMatrixes(int m, int k) {
        accuracy = new double[m - 1][k];
        for(int i=0; i<2; i++){
            precision.add(new double[m - 1][k]);
            recall.add(new double[m - 1][k]);
            fMeasure.add(new double[m - 1][k]);
        }
    }
    
    /**
     * Установить элемент матрицы точностей.
     * @param testingAmt кол-во каталогов, на которых проводится тестирование.
     * @param foldNumber номер прогона в K-fold кроссвалидации.
     * @param accuracy значение метрики.
     */
    public void setAccuracy(int testingAmt, int foldNumber, double accuracy){
        this.accuracy[testingAmt - 1][foldNumber - 1] = accuracy;
    }
    
    /**
     * Установить элемент матрицы прецизионностей.
     * @param testingAmt кол-во каталогов, на которых проводится тестирование.
     * @param foldNumber номер прогона в K-fold кроссвалидации.
     * @param precision значение метрики.
     * @param isSpam класс (SPAM или HAM).
     */
    public void setPrecision(int testingAmt, int foldNumber, double precision, boolean isSpam){
        setMetric(testingAmt, foldNumber, precision, isSpam, 0);
    }
    
    /**
     * Установить элемент матрицы полноты.
     * @param testingAmt кол-во каталогов, на которых проводится тестирование.
     * @param foldNumber номер прогона в K-fold кроссвалидации.
     * @param recall значение метрики.
     * @param isSpam класс (SPAM или HAM).
     */
    public void setRecall(int testingAmt, int foldNumber, double recall, boolean isSpam){
        setMetric(testingAmt, foldNumber, recall, isSpam, 1);
    }
    
    /**
     * Установить элемент матрицы F-мер.
     * @param testingAmt кол-во каталогов, на которых проводится тестирование.
     * @param foldNumber номер прогона в K-fold кроссвалидации.
     * @param fMeasure значение метрики.
     * @param isSpam класс (SPAM или HAM).
     */
    public void setFMeasure(int testingAmt, int foldNumber, double fMeasure, boolean isSpam){
        setMetric(testingAmt, foldNumber, fMeasure, isSpam, 2);
    }
    
    /**
     * Установить значение метрики.
     * @param testingAmt кол-во каталогов, на которых проводится тестирование.
     * @param foldNumber номер прогона в K-fold кроссвалидации.
     * @param metric значение метрики.
     * @param isSpam класс (SPAM или HAM).
     * @param mode тип метрики:
     * <ol>
     * <li>прецизионность;</li>
     * <li>полнота;</li>
     * <li>F-мера.</li>
     * </ol>
     */
    private void setMetric(int testingAmt, int foldNumber, double metric, boolean isSpam, int mode){
        int spam = booleanToInteger(isSpam);
        switch(mode){
            case 0:{
                precision.get(spam)[testingAmt - 1][foldNumber - 1] = metric;
                break;
            }
            case 1:{
                recall.get(spam)[testingAmt - 1][foldNumber - 1] = metric;
                break;
            }
            case 2:{
                fMeasure.get(spam)[testingAmt - 1][foldNumber - 1] = metric;
                break;
            }
        }
    }
    
    /**
     * Перевод булевского значения в целочисленное.
     * @param value булевское значение.
     * @return соответствующее целочисленное значение.
     */
    private static int booleanToInteger(boolean value){
        if(value){
            return 1;
        }
        return 0;
    }
    
    /**
     * Получить матрицу точностей.
     * @return матрица точностей.
     */
    public double[][] getAccuracy() {
        return accuracy;
    }
    
    /**
     * Получить матрицу прецизионностей.
     * @param isSpam класс (SPAM или HAM).
     * @return матрица прецизионностей.
     */
    public double[][] getPrecision(boolean isSpam){
        return precision.get(booleanToInteger(isSpam));
    }
    
    /**
     * Получить матрицу полноты.
     * @param isSpam класс (SPAM или HAM).
     * @return матрица полноты.
     */
    public double[][] getRecall(boolean isSpam){
        return recall.get(booleanToInteger(isSpam));
    }
    
    /**
     * Получить матрицу F-мер.
     * @param isSpam класс (SPAM или HAM).
     * @return матрица F-мер.
     */
    public double[][] getFMeasure(boolean isSpam){
        return fMeasure.get(booleanToInteger(isSpam));
    }
    
    /**
     * Установка всех метрик для данного цикла тестирования.
     * @param cest метрики.
     * @param testingAmt кол-во каталогов, на которых проводится тестирование.
     * @param foldNumber номер прогона в K-fold кроссвалидации.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public void setAllMetrics(ClassificationEstimates cest, int testingAmt, int foldNumber) throws SQLException, ClassNotFoundException{
        accuracy[testingAmt - 1][foldNumber - 1] = cest.computeAccuracy();
        List<Double> estimates = cest.computeEstimates();
        precision.get(1)[testingAmt - 1][foldNumber - 1] = estimates.get(0);
        recall.get(1)[testingAmt - 1][foldNumber - 1] = estimates.get(1);
        precision.get(0)[testingAmt - 1][foldNumber - 1] = estimates.get(2);
        recall.get(0)[testingAmt - 1][foldNumber - 1] = estimates.get(3);
        List<Double> measures = cest.computeFMeasure(estimates, BETA);
        fMeasure.get(1)[testingAmt - 1][foldNumber - 1] = measures.get(0);
        fMeasure.get(0)[testingAmt - 1][foldNumber - 1] = measures.get(1);
    }
    
    private List<Double> getAverage(int meticType){
        List<Double> averages = new ArrayList<>();
        switch(meticType){
            case 0:{
                for(double[] el: accuracy){
                    averages.add(Utils.average(el));
                }
                break;
            }
            case 1:
            case 2:{
                for(double[] el: precision.get(meticType - 1)){
                    averages.add(Utils.average(el));
                }
                break;
            }
            case 3:
            case 4:{
                for(double[] el: recall.get(meticType - 3)){
                    averages.add(Utils.average(el));
                }
                break;
            }
            case 5:
            case 6:{
                for(double[] el: fMeasure.get(meticType - 5)){
                    averages.add(Utils.average(el));
                }
                break;
            }
        }
        return averages;
    }
    
    public List<Double> getAverageAccuracy(){
        return getAverage(0);
    }
    
    public List<Double> getAveragePrecision(boolean isSpam){
        if(isSpam){
            return getAverage(2);
        }
        return getAverage(1);
    }
    
    public List<Double> getAverageRecall(boolean isSpam){
        if(isSpam){
            return getAverage(4);
        }
        return getAverage(3);
    }
    
    public List<Double> getAverageFMeasure(boolean isSpam){
        if(isSpam){
            return getAverage(6);
        }
        return getAverage(5);
    }
    
    public int getM(){
        return accuracy.length;
    }
    
    public int getK(){
        return accuracy[0].length;
    }
}
