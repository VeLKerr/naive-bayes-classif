package com.naivebayesclassifier.curves;

import static com.naivebayesclassifier.Main.PART_NUMBER;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Класс для создания Learning Curves и вывода их на экран в виде
 * swing-окна.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class Curves extends JFrame{
    /**
     * Абсциссы. Размер обучающей выборки.
     */
    private static final int[] xData = new int[PART_NUMBER];
    /**
     * Названия графиков.
     */
    private static final String[] curveNames = {"Accuracy", "Precision", "Recall", "fMeasure"};
    /**
     * Названия классов сообщений.
     */
    private static final String[] classNames = {"Spam", "Ham"};
    /**
     * Ординаты графиков.
     */
    private final List<double[]> yData;
    /**
     * Номер графика.
     * <ul>
     * <li>0 - Accuracy;</li>
     * <li>1 - Precision;</li>
     * <li>2 - Recall;</li>
     * <li>3 - fMeasure;</li>
     * </ul>
     */
    private final int number;
    
    /**
     * Построить название кривой на графике.
     * @param curveNumber номер графика.
     * @param classNumber номер класса.
     * @return 
     */
    private static String buildComponentName(int curveNumber, int classNumber){
        StringBuilder sb = new StringBuilder();
        sb.append(curveNames[curveNumber]).append("(").append(classNames[classNumber]).append(")");
        return sb.toString();
    }
    
    /**
     * Создать массив точек на оси абсцисс.
     */
    private static void fillXData(){
        for(int i=0; i<PART_NUMBER; i++){
            xData[i] = i;
        }
    }

    /**
     * Создать swing-окно JFrame с графиком.
     * @param number номер графика.
     * <ul>
     * <li>0 - Accuracy;</li>
     * <li>1 - Precision;</li>
     * <li>2 - Recall;</li>
     * <li>3 - fMeasure;</li>
     * </ul>
     * @param yData массив ординат.
     */
    public Curves(int number, double[]... yData) {
        fillXData();
        this.yData = new ArrayList<>();
        this.yData.addAll(Arrays.asList(yData));
        this.number = number;
        JFreeChart chart = createChart();
        ChartPanel cpanel = new ChartPanel(chart);
        getContentPane().add(cpanel, BorderLayout.CENTER);
    }
    
    /**
     * Построить график.
     * @param components названия кривых.
     * @param dots ординаты.
     * @return коллекция кривых.
     */
    private XYSeriesCollection createData(List<String> components, List<double[]> dots){
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for(int i=0; i<components.size(); i++){
            XYSeries series = new XYSeries(components.get(i));
            for(int j=0; j<PART_NUMBER; j++){
                series.add(xData[j], dots.get(i)[j]);
            }
            xySeriesCollection.addSeries(series);
        }
        return xySeriesCollection;
    }
    
    /**
     * Нарисовать график.
     * @return форма с графиком.
     */
    private JFreeChart createChart(){
        List<String> components = new ArrayList<>();
        if(number == 0){
            components.add(curveNames[0]);
        }
        else{
            for(int i=0; i<classNames.length; i++){
                components.add(buildComponentName(number, i));
            }
        }
        XYDataset data = (XYDataset)createData(components, yData);
        JFreeChart chart = ChartFactory.createXYLineChart(curveNames[number], "LearningV", curveNames[number], data,
                PlotOrientation.VERTICAL, true, true, false);
        XYPlot plot = chart.getXYPlot();
        Font font  = new Font( "Meiryo", Font.PLAIN, 12);
        Font font2 = new Font( "Meiryo", Font.PLAIN, 8);
        chart.getLegend().setItemFont(font);
        chart.getTitle().setFont(font);
        XYPlot xyp = chart.getXYPlot();
        xyp.getDomainAxis().setLabelFont(font); // X
        xyp.getRangeAxis().setLabelFont(font); // Y
        xyp.getDomainAxis().setTickLabelFont(font2);
        xyp.getRangeAxis().setTickLabelFont(font2);
        xyp.getDomainAxis().setVerticalTickLabels(true);

        //Заполнение и линии.
        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer)plot.getRenderer();
        r.setSeriesOutlinePaint(0, Color.RED);
        r.setSeriesOutlinePaint(1, Color.BLUE);
        r.setSeriesShapesFilled(0, false);
        r.setSeriesShapesFilled(1, false);
        return chart;
    }
    
    /**
     * Построить график.
     * @param number номер графика.
     * <ul>
     * <li>0 - Accuracy;</li>
     * <li>1 - Precision;</li>
     * <li>2 - Recall;</li>
     * <li>3 - fMeasure;</li>
     * </ul>
     * @param yData массив ординат.
     */
    public static void create(int number, double[]... yData){
        Curves frame = new Curves(number, yData);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(10, 10, 500, 500);
        frame.setTitle(curveNames[number]);
        frame.setVisible(true);
    }
}
