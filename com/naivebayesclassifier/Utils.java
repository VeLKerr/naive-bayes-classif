/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.naivebayesclassifier;

import static com.naivebayesclassifier.Main.PART_NUMBER;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 *
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public abstract class Utils {
    
    /**
     * Округление десятичной дроби
     * @param value дробь
     * @param symbolsAfterKoma кол-во знаков после запятой
     * @return округлённая дробь
     */
    public static double roundDouble(double value, int symbolsAfterKoma){
        return new BigDecimal(value).setScale(symbolsAfterKoma, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * В некоторых случаях, при вычислениях точности и полноты, возникает
     * значение NaN (Например, если ни одного сообщения во время тестирования
     * не было отнесено к классу SPAM). На самом деле, этот результат можно
     * интерпретировать как 0. Данный метод заменяет NaN на 0.
     * @param value число, которое проверяется на NaN
     * @return заменённое (если необходимо) число.
     */
    public static double nanToZero(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        return value;
    }
    
    
    public static double average(List<Double> values){
        double val = 0.0;
        for(double v: values){
            val += v;
        }
        return val / PART_NUMBER;
    }
}
