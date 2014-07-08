package com.naivebayesclassifier;

import com.naivebayesclassifier.dao.ClassifiedMessages;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Оценки роботы алгоритма классификации.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class ClassificationEstimates {
    private final int testingDataSetNumber; //Номер папки набором сообщений, предназначенных для тестировки.

    public ClassificationEstimates(int testingDataSetNumber) {
        this.testingDataSetNumber = testingDataSetNumber;
    }
    
    /**
     * Получить кол-во сообщений тестировчного набора.
     * @return кол-во сообщений тестировчного набора.
     */
    private int getRetrievedFilesCount(){
        return new File(Main.buildPath(testingDataSetNumber)).list().length;
    }
    
    /**
     * Рассчёт метрики <b>accuracy</b>
     * @return значение метрики.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public double computeAccuracy() throws SQLException, ClassNotFoundException{
        return (double)ClassifiedMessages.getRelevantCount() / (double)getRetrievedFilesCount();
    }
    
    /**
     * Рассчёт точности и полноты алгоритма.
     * @return список из 4-х значений метрик:
     * <ul>
     *  <li>Точность (Precision) для класса SPAM</li>
     *  <li>Полнота (Recall) для класса SPAM</li>
     *  <li>Точность (Precision) для класса HAM</li>
     *  <li>Полнота (Recall) для класса HAM</li>
     * </ul>
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public List<Double> computeEstimates() throws SQLException, ClassNotFoundException {
        List<Double> estimates = new ArrayList<>(4);
        Connection conn = ConnectionProperties.createConnection();
        double spamSpam = ClassifiedMessages.count(conn, true, true);
        double hamHam = ClassifiedMessages.count(conn, false, false);
        double sysSpam = ClassifiedMessages.count(conn, false, true);
        double sysHam = ClassifiedMessages.count(conn, true, false);
        estimates.add(nanToZero(spamSpam / (spamSpam + sysSpam)));
        estimates.add(nanToZero(spamSpam / (spamSpam + sysHam)));
        estimates.add(nanToZero(hamHam / (hamHam + sysHam)));
        estimates.add(nanToZero(hamHam / (hamHam + sysSpam)));
        return estimates;
    }
    
    /**
     * Рассчёт F-меры (F-measure)
     * @param estimates оценки точности и полноты в том порядке, в каком они
     * выдаются методом @see computeEstimates().
     * @param beta приоритет метрик. Если <code>beta</code> є [0;1], приоритет
     * отдаётся точности. Если <code>beta</code> > 1, приоритет отдаётся полноте.
     * Если же <code>beta</code> = 1, эти две метрики имеют равный приоритет.
     * @return значение F-меры
     */
    public List<Double> computeFMeasure(List<Double> estimates, double beta){
        List<Double> measures = new ArrayList<>(2);
        measures.add(computeF(estimates.get(0), estimates.get(1), beta));
        measures.add(computeF(estimates.get(2), estimates.get(3), beta));
        return measures;
    }
    
    /**
     * Реализация формулы вычисления F-меры.
     * @param precision точность
     * @param recall полнота
     * @param beta приоритет (вес) метрик.
     * @return значение F-меры.
     */
    private double computeF(double precision, double recall, double beta){
        double powBeta = Math.pow(beta, 2.0);
        return (powBeta + 1) * precision * recall / (powBeta * precision + recall);
    }
    
    /**
     * В некоторых случаях, при вычислениях точности и полноты, возникает
     * значение NaN (Например, если ни одного сообщения во время тестирования
     * не было отнесено к классу SPAM). На самом деле, этот результат можно
     * интерпретировать как 0. Данный метод заменяет NaN на 0.
     * @param value число, которое проверяется на NaN
     * @return заменённое (если необходимо) число.
     */
    private double nanToZero(double value){
        if(value == Double.NaN){
            return 0.0;
        }
        return value;
    }
}
