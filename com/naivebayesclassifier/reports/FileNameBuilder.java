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
    private static final String FNAME_ENDING = ".xls";
    
    /**
     * Получить префикс файла отчёта.
     * @param isGeneral тип отчёта. Если <code>true</code>, отчёт по исследованию
     * (K-fold кроссвалидация). Иначе - отчёт по тестированию.
     * @return префикс.
     */
    private static String getFnameStarting(boolean isGeneral){
        if(isGeneral){
            return "Gen_";
        }
        return "Res_";
    }
    
    /**
     * Текстовое представление текщих времени и даты.
     * @return текстовое представление.
     */
    private static String setCurrentDate(){
        return new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());
    }
    
    /**
     * Построить название файла отчёта.
     * @param isGeneral тип отчёта. Если <code>true</code>, отчёт по исследованию
     * (K-fold кроссвалидация). Иначе - отчёт по тестированию.
     * @return строка с названием файла.
     */
    public static String buildFName(boolean isGeneral){
        StringBuilder sb = new StringBuilder(getFnameStarting(isGeneral));
        sb.append(setCurrentDate()).append(FNAME_ENDING);
        return sb.toString();
    }
}
