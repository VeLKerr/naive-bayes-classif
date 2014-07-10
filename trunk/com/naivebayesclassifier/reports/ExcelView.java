/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.naivebayesclassifier.reports;

import static com.naivebayesclassifier.Main.PART_NUMBER;
import com.naivebayesclassifier.Utils;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.eclipse.persistence.jpa.rs.MatrixParameters;

/**
 *
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class ExcelView {
    private static final String CORNER_CELL_TEXT = "Learn \\ Test";
    private static final String[] sheetNames = {
        "Accuracy", "Precision", "Recall", "F-measure"
    };
    
    /**
     * Автоматическое открытие файла с отчётом.
     * <b>Работает только на Windows!</b>
     * @param filename имя файла.
     */
    private static void automaticallyOpenFile(String filename) { //функция которая автоматически открывает файл с указанным именем
        String command = "cmd /c start " + filename; //созадем строковую переменую, которая содержит относительный путь к файлу
        try {
            Process child = Runtime.getRuntime().exec(command); //запускаем в текущем времмени эту команду
        } catch (IOException ex) {
            System.err.println("Could not open report file!");
        }
    }
    
    public static void generateReport(MetricMatrixes mm){
        automaticallyOpenFile(generateRep(mm));
    }
    
    private static String generateRep(MetricMatrixes mm){
        FileOutputStream fileOut = null;
        String fname = FileNameBuilder.buildFName();
        try {
            fileOut = new FileOutputStream(fname);
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFCellStyle mainHeader = workbook.createCellStyle();
            mainHeader = setMainHeadStyle(mainHeader);
            HSSFCellStyle usual = workbook.createCellStyle();
            usual = setUsualStyle(usual);
            HSSFCellStyle corner = workbook.createCellStyle();
            corner = setCornerStyle(corner);
            HSSFCellStyle header = workbook.createCellStyle();
            header = setHeaderCellsStyle(header);
            HSSFSheet workSheet = null;
            for(int i=0; i<sheetNames.length; i++){
                workSheet = workbook.createSheet(sheetNames[i]);
                HSSFRow row = workSheet.createRow(0);
                HSSFCell cell = row.createCell(0);
                cell.setCellStyle(mainHeader);
                switch(i){
                    case 0:{
                        int rowCnt = 0;
                        cell.setCellValue(sheetNames[i]);
                        CellRangeAddress region = new CellRangeAddress(rowCnt, rowCnt, 0, PART_NUMBER - 1);
                        workSheet.addMergedRegion(region);
                        rowCnt++;
                        matrixView(workSheet, mm.getAccuracy(), rowCnt, usual, corner, header);
                        break;
                    }
                    case 1:
                    case 2:
                    case 3:{
                        int rowCnt = 0;
                        cell.setCellValue(sheetNames[i] + "(Spam)");
                        CellRangeAddress region = new CellRangeAddress(rowCnt, rowCnt, 0, PART_NUMBER - 1);
                        workSheet.addMergedRegion(region);
                        rowCnt++;
                        matrixView(workSheet, mm.getPrecision(true), rowCnt, usual, corner, header);
                        rowCnt += PART_NUMBER + 1;
                        row = workSheet.createRow(rowCnt);
                        cell = row.createCell(0);
                        cell.setCellStyle(mainHeader);
                        cell.setCellValue(sheetNames[i] + "(Ham)");
                        region = new CellRangeAddress(rowCnt, rowCnt, 0, PART_NUMBER - 1);
                        workSheet.addMergedRegion(region);
                        rowCnt++;
                        matrixView(workSheet, mm.getPrecision(false), rowCnt, usual, corner, header);
                        break;
                    }
                }
            }
            workbook.write(fileOut);
            fileOut.flush();
        } catch (IOException ex) {
            Logger.getLogger(ExcelView.class.getName()).log(Level.SEVERE, "File not found!", ex);
        }
        return fname;
    }
    
    private static void matrixView(HSSFSheet workSheet, double [][] matrix,
                            int firstRowIndex, HSSFCellStyle usual,
                            HSSFCellStyle corner, HSSFCellStyle header){
        HSSFRow row = workSheet.createRow(firstRowIndex);
        HSSFCell cell = null;
        for(int j=0; j<=matrix.length; j++){
            cell = row.createCell(j);
            if(j != 0){
                cell.setCellStyle(header);
                cell.setCellValue(j);
            }
            else{
                cell.setCellStyle(corner);
                cell.setCellValue(CORNER_CELL_TEXT);
            }
        }
        for(int i=1; i<=matrix.length; i++){
            row = workSheet.createRow(firstRowIndex + i);
            row.setRowStyle(usual);
            for(int j=0; j<=matrix[0].length; j++){
                cell = row.createCell(j);
                if(j != 0){
                    cell.setCellStyle(usual);
                    cell.setCellValue(Utils.roundDouble(Utils.nanToZero(matrix[i - 1][j - 1]), 4));
                }
                else{
                    cell.setCellStyle(header);
                    cell.setCellValue(i);
                }
            }
        }
    }
    
    private static HSSFCellStyle setMainHeadStyle(HSSFCellStyle cellStyle) { /*функция которая назначает стиль верхней строки*/
        cellStyle.setFillForegroundColor(HSSFColor.RED.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return cellStyle;
    }
    
    private static HSSFCellStyle setCornerStyle(HSSFCellStyle cellStyle){
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index); /*устанавливает фон*/
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); 
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);  /*эти значения аналогичны тем, что в предидущей функции толкьо с другими параметрами*/
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);/*высоту ячейки*/
        return cellStyle;  /*возвращает стиль ячейки*/
    }

    private static HSSFCellStyle setUsualStyle(HSSFCellStyle cellStyle) {/*функция которая назначает стиль всех остальных ячеек.*/
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);  /*эти значения аналогичны тем, что в предидущей функции толкьо с другими параметрами*/
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return cellStyle;
    }
    
    private static HSSFCellStyle setHeaderCellsStyle(HSSFCellStyle cellStyle) {
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return cellStyle;
    }
    
    private static HSSFCellStyle setNewAdditionalCellsStyle(HSSFCellStyle cellStyle) {
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return cellStyle;
    }
}
