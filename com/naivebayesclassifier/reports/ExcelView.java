package com.naivebayesclassifier.reports;

import com.naivebayesclassifier.ClassificationEstimates;
import static com.naivebayesclassifier.Main.PART_NUMBER;
import com.naivebayesclassifier.Utils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Класс, отвечающий за генерацию Excel-отчётов по результатам исследования.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class ExcelView {
    /**
     * Текст угловой ячейки листа с матрицей.
     */
    private static final String CORNER_CELL_TEXT = "Learn \\ Test";
    
    /**
     * Названия Excel-листов.
     */
    private static final String[] SHEET_NAMES = {
        "Accuracy", "Precision", "Recall", "F-measure"
    };
    
    /**
     * Названия классов сообщений.
     */
    private static final String[] CLASSES = {"Spam", "Ham"};
    
    /**
     * Константа, показывающая сколько знаков после запятой оставлять при
     * округлении матриц с метриками.
     */
    private static final int SYMBOLS_AFTER_KOMA = 4;
    
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
    
    /**
     * Генерация отчёта по режиму тестирования.
     * @param cest объект для рассчёта метрик.
     * @param beta коэффициент BETA для рассчёта F-меры.
     */
    public static void generateTestingReport(ClassificationEstimates cest, double beta){
        automaticallyOpenFile(generateTestingRep(cest, beta));
    }
    
    /**
     * Генерация отчёта по режиму тестирования.
     * @param cest объект для рассчёта метрик.
     * @param beta коэффициент BETA для рассчёта F-меры.
     */
    private static String generateTestingRep(ClassificationEstimates cest, double beta){
        FileOutputStream fileOut = null;
        String fname = FileNameBuilder.buildFName(false);
        try{
            fileOut = new FileOutputStream(fname);
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet workSheet = workbook.createSheet("Estimates");
            
            HSSFCellStyle mainHeader = workbook.createCellStyle();
            mainHeader = setMainHeadStyle(mainHeader);
            HSSFCellStyle usual = workbook.createCellStyle();
            usual = setUsualStyle(usual);
            HSSFCellStyle accuracyHeader = workbook.createCellStyle();
            accuracyHeader = setCornerStyle(accuracyHeader);
            HSSFCellStyle fMeasureHeader = workbook.createCellStyle();
            fMeasureHeader = setNewAdditionalCellsStyle(fMeasureHeader);
            HSSFCellStyle preRecHeader = workbook.createCellStyle();
            preRecHeader = setHeaderCellsStyle(preRecHeader);
            HSSFCellStyle spamHeaderCellStyle = workbook.createCellStyle();
            spamHeaderCellStyle = setSpamCellsStyle(spamHeaderCellStyle);
            
            HSSFRow row = workSheet.createRow(0);
            HSSFCell cell = row.createCell(0);
            cell.setCellStyle(mainHeader);
            cell.setCellValue("Testing folders: ");
            cell = row.createCell(1);
            cell.setCellStyle(usual);
            cell.setCellValue(cest.getTestingDataSetNumbers().toString());
            
            row = workSheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellStyle(accuracyHeader);
            cell.setCellValue(SHEET_NAMES[0]);
            cell = row.createCell(1);
            cell.setCellStyle(usual);
            cell.setCellValue(cest.computeAccuracy());
            
            row = workSheet.createRow(2);
            for(int i=0; i<CLASSES.length; i++){
                cell = row.createCell(i + 1);
                cell.setCellStyle(spamHeaderCellStyle);
                cell.setCellValue(CLASSES[i]);
            }
            
            List<Double> estimates = cest.computeEstimates();
            
            for(int i=0; i<2; i++){
                row = workSheet.createRow(i + 3);
                cell = row.createCell(0);
                cell.setCellStyle(preRecHeader);
                cell.setCellValue(SHEET_NAMES[i + 1]);
                for(int j=0; j<CLASSES.length; j++){
                    cell = row.createCell(j + 1);
                    cell.setCellStyle(usual);
                    cell.setCellValue(estimates.get(2 * j + i));
                }
            }
            
            List<Double> fMeasures = cest.computeFMeasure(estimates, beta);
            
            row = workSheet.createRow(5);
            cell = row.createCell(0);
            cell.setCellStyle(fMeasureHeader);
            cell.setCellValue(SHEET_NAMES[3]);
            for(int j=0; j<CLASSES.length; j++){
                cell = row.createCell(j + 1);
                cell.setCellStyle(usual);
                cell.setCellValue(fMeasures.get(j));
            }
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        }
        catch (IOException | SQLException | ClassNotFoundException ex) {
            Logger.getLogger(ExcelView.class.getName()).log(Level.SEVERE, "File not found!", ex);
        }
        return fname;
    }
    
    /**
     * Сгенерировать отчёт по режиму исследования.
     * @param mm матрицы с метриками.
     */
    public static void generateGeneralReport(MetricMatrixes mm){
        automaticallyOpenFile(generateGeneralRep(mm));
    }
    
    /**
     * Сгенерировать отчёт по режиму исследования.
     * @param mm матрицы с метриками.
     */
    private static String generateGeneralRep(MetricMatrixes mm){
        FileOutputStream fileOut = null;
        String fname = FileNameBuilder.buildFName(true);
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
            for(int i=0; i<SHEET_NAMES.length; i++){
                workSheet = workbook.createSheet(SHEET_NAMES[i]);
                HSSFRow row = workSheet.createRow(0);
                HSSFCell cell = row.createCell(0);
                cell.setCellStyle(mainHeader);
                switch(i){
                    case 0:{
                        int rowCnt = 0;
                        cell.setCellValue(SHEET_NAMES[i]);
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
                        cell.setCellValue(SHEET_NAMES[i] + "(" + CLASSES[0] + ")");
                        CellRangeAddress region = new CellRangeAddress(rowCnt, rowCnt, 0, PART_NUMBER - 1);
                        workSheet.addMergedRegion(region);
                        rowCnt++;
                        matrixView(workSheet, mm.getPrecision(true), rowCnt, usual, corner, header);
                        rowCnt += PART_NUMBER + 1;
                        row = workSheet.createRow(rowCnt);
                        cell = row.createCell(0);
                        cell.setCellStyle(mainHeader);
                        cell.setCellValue(SHEET_NAMES[i] + "(" + CLASSES[1] + ")");
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
            fileOut.close();
        } 
        catch (IOException ex) {
            Logger.getLogger(ExcelView.class.getName()).log(Level.SEVERE, "File not found!", ex);
        }
        return fname;
    }
    
    /**
     * Представление матрицы в Excel-файле.
     * @param workSheet Excel-лист, где должна располагаться матрица.
     * @param matrix матрица.
     * @param firstRowIndex номер строки, начиная с которой будет выводится матрица.
     * @param usual <i>стиль</i> ячейки с элементом матрицы.
     * @param corner <i>стиль</i> ячейки с заголовком строки или столбца.
     * @param header <i>стиль</i> углового элемента.
     */
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
                    cell.setCellValue(Utils.roundDouble(
                            Utils.nanToZero(matrix[i - 1][j - 1]), SYMBOLS_AFTER_KOMA));
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
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);  /*эти значения аналогичны тем, что в предидущей функции только с другими параметрами*/
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);/*высоту ячейки*/
        return cellStyle;  /*возвращает стиль ячейки*/
    }

    private static HSSFCellStyle setUsualStyle(HSSFCellStyle cellStyle) {/*функция которая назначает стиль всех остальных ячеек.*/
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);  /*эти значения аналогичны тем, что в предидущей функции только с другими параметрами*/
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
    
    private static HSSFCellStyle setSpamCellsStyle(HSSFCellStyle cellStyle) {
        cellStyle.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return cellStyle;
    }
}
