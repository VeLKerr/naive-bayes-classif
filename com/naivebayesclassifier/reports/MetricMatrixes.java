/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.naivebayesclassifier.reports;

import static com.naivebayesclassifier.Main.PART_NUMBER;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для хранения матриц с метриками. Будет использоваться для построения Learning Curves.
 * Каждая матрица имеет размерность 9*9, при том заполняется только её часть над побочной
 * диагональю.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class MetricMatrixes {
    /**
     * Матрица точностей.
     */
    private final double[][] accuracy = new double[PART_NUMBER - 1][PART_NUMBER - 1];
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

    public MetricMatrixes() {
        for(int i=0; i<2; i++){
            precision.add(new double[PART_NUMBER - 1][PART_NUMBER - 1]);
            recall.add(new double[PART_NUMBER - 1][PART_NUMBER - 1]);
            fMeasure.add(new double[PART_NUMBER - 1][PART_NUMBER - 1]);
        }
    }
    
    /**
     * Установить элемент матрицы точностей.
     * @param learningAmt кол-во каталогов, на которых система обучалась.
     * @param testingAmt кол-во каталогов, на которых производилось тестирование.
     * @param accuracy значение метрики.
     */
    public void setAccuracy(int learningAmt, int testingAmt, double accuracy){
        this.accuracy[learningAmt - 1][testingAmt - 1] = accuracy;
    }
    
    /**
     * Установить элемент матрицы прецизионностей.
     * @param learningAmt кол-во каталогов, на которых система обучалась.
     * @param testingAmt кол-во каталогов, на которых производилось тестирование.
     * @param precision значение метрики.
     * @param isSpam класс (SPAM или HAM).
     */
    public void setPrecision(int learningAmt, int testingAmt, double precision, boolean isSpam){
        setMetric(learningAmt, testingAmt, precision, isSpam, 0);
    }
    
    /**
     * Установить элемент матрицы полноты.
     * @param learningAmt кол-во каталогов, на которых система обучалась.
     * @param testingAmt кол-во каталогов, на которых производилось тестирование.
     * @param recall значение метрики.
     * @param isSpam класс (SPAM или HAM).
     */
    public void setRecall(int learningAmt, int testingAmt, double recall, boolean isSpam){
        setMetric(learningAmt, testingAmt, recall, isSpam, 1);
    }
    
    /**
     * Установить элемент матрицы F-мер.
     * @param learningAmt кол-во каталогов, на которых система обучалась.
     * @param testingAmt кол-во каталогов, на которых производилось тестирование.
     * @param fMeasure значение метрики.
     * @param isSpam класс (SPAM или HAM).
     */
    public void setFMeasure(int learningAmt, int testingAmt, double fMeasure, boolean isSpam){
        setMetric(learningAmt, testingAmt, fMeasure, isSpam, 2);
    }
    
    /**
     * Установить значение метрики.
     * @param learningAmt кол-во каталогов, на которых система обучалась.
     * @param testingAmt кол-во каталогов, на которых производилось тестирование.
     * @param metric значение метрики.
     * @param isSpam класс (SPAM или HAM).
     * @param mode тип метрики:
     * <ol>
     * <li>прецизионность;</li>
     * <li>полнота;</li>
     * <li>F-мера.</li>
     * </ol>
     */
    private void setMetric(int learningAmt, int testingAmt, double metric, boolean isSpam, int mode){
        int spam = booleanToInteger(isSpam);
        switch(mode){
            case 0:{
                precision.get(spam)[learningAmt - 1][testingAmt - 1] = metric;
                break;
            }
            case 1:{
                recall.get(spam)[learningAmt - 1][testingAmt - 1] = metric;
                break;
            }
            case 2:{
                fMeasure.get(spam)[learningAmt - 1][testingAmt - 1] = metric;
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
}
