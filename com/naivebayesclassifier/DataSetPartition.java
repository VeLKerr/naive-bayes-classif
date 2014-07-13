package com.naivebayesclassifier;

import java.util.List;
import static com.naivebayesclassifier.Main.PART_NUMBER;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Класс, автоматизирующий разбиение набора данных на обучающую и тестировочную
 * выборки.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class DataSetPartition {
    private final List<Integer> learning;
    private final List<Integer> testing;
    private int testingSize;

    /**
     * Создать разбиение.
     */
    public DataSetPartition() {
        learning = new ArrayList<>();
        testing = new ArrayList<>();
    }
    
    /**
     * Установить разбиение в начальное положение:
     * |  Learning  |Test.|,
     * т.е. для тестировочной выборки берутся каталоги с максимальными номерами.
     */
    private void setInitial(){
        for(int i=0; i<testingSize; i++){
            testing.add(PART_NUMBER - i);
        }
        for(int j=1; j<=PART_NUMBER - testingSize; j++){
            learning.add(j);
        }
    }
    
    /**
     * Установить размер тестировочной выборки.
     * @param testingSize размер тестировочной выборки.
     */
    public void setTestingSize(int testingSize) {
        this.testingSize = testingSize;
        setInitial();
    }
    
    /**
     * Сделать разбиение для следующего разбиения (см. 
     * Описание проекта - принцип K-fold кроссвалидации).
     */
    public void nextExperiment(){
        int max = Collections.max(testing);
        int min = Collections.min(testing);
        if(min > 1){
            testing.remove(max);
            learning.add(max);
            testing.add(min - 1);
            learning.remove(min - 1);
        }
        else
            if(min == 1 && max != PART_NUMBER){
                testing.remove(max);
                learning.add(max);
                testing.add(PART_NUMBER);
                learning.remove(PART_NUMBER);
            }
            else{
                int learnMax = Collections.max(learning);
                int learnMin = Collections.min(learning);
                testing.add(learnMax);
                learning.remove(learnMax);
                testing.remove(learnMin - 1);
                learning.add(learnMin - 1);
            }
    }
    
    /**
     * Получить номера каталогов, которые в данном эксперименте выступают в роли
     * <b>обучающей</b> выборки.
     * @return 
     */
    public List<Integer> getLearning() {
        return learning;
    }
    
    /**
     * Получить номера каталогов, которые в данном эксперименте выступают в роли
     * <b>тестировочной</b> выборки.
     * @return 
     */
    public List<Integer> getTesting() {
        return testing;
    }
    
}
