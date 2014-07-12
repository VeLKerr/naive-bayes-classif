package com.naivebayesclassifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс функций-математических утилит.
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
    
    /**
     * Рассчёт среднего значения списка чисел.
     * @param values список чисел.
     * @return среднее значение.
     */
    public static double average(double[] values){
        double val = 0.0;
        for(double v: values){
            val += v;
        }
        return val / values.length;
    }

    /**
     * Ввести целое число.
     * @param min минимальная граница вводимых чисел.
     * @param max максимальная граница.
     * @return полученное число.
     * @throws IOException
     */
    public static int inputNumber(BufferedReader br, int min, int max) throws IOException{
        int number = 0;
        while (number == 0) {
            try {
                number = Integer.parseInt(br.readLine());
                if (number < min || number > max) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfe) {
                number = 0;
                System.out.print("\tIncorrect! Input once more: ");
            }
        }
        return number;
    }
    
    /**
     * Ввести действительное число.
     * @param min минимальная граница вводимых чисел.
     * @param max максимальная граница.
     * @return полученное число.
     * @throws IOException
     */
    public static double inputDouble(BufferedReader br, double min, double max) throws IOException{
        double number = 0.0;
        while (number == 0.0) {
            try {
                number = Double.parseDouble(br.readLine());
                if (number < min || number > max) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfe) {
                number = 0.0;
                System.out.print("\tIncorrect! Input once more: ");
            }
        }
        return number;
    }
    
    public static List<Integer> inputListNumbers(BufferedReader br, int min, int max) throws IOException{
        List<Integer> numbers = new ArrayList<>();
        while(true){
            numbers.add(inputNumber(br, min, max));
            System.out.println("If you want to input number onece more, press \'Y\'. ");
            System.out.print("Otherwise, press \'N\'");
            String answStr = br.readLine();
            if(answStr.equalsIgnoreCase("n")){
                break;
            }
        }
        return numbers;
    }
}
