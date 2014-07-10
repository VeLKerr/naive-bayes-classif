package com.naivebayesclassifier.reports;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс для построения названия файла отчёта.
 * Название строится на основе текущего времени
 * чтобы следующий сгенерированный отчёт не затирал предыдущий.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class FileNameBuilder {
    private static final String FNAME_STARTING = "Res_";
    private static final String FNAME_ENDING = ".xls";
    
    /**
     * Текстовое представление текщих времени и даты.
     * @return текстовое представление.
     */
    private static String setCurrentDate(){
        return new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());
    }
    
    /**
     * Построить название файла отчёта.
     * @return строка с названием файла.
     */
    public static String buildFName(){
        StringBuilder sb = new StringBuilder(FNAME_STARTING);
        sb.append(setCurrentDate()).append(FNAME_ENDING);
        return sb.toString();
    }
}
