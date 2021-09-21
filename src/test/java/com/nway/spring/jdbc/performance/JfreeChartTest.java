package com.nway.spring.jdbc.performance;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class JfreeChartTest {

    @Test
    public void lineTest() throws IOException {

        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        line_chart_dataset.addValue(15, "mybatis", "1970");
        line_chart_dataset.addValue(28, "mybatis", "1980");
        line_chart_dataset.addValue(59, "mybatis", "1990");
        line_chart_dataset.addValue(110, "mybatis", "2000");
        line_chart_dataset.addValue(220, "mybatis", "2010");
        line_chart_dataset.addValue(290, "mybatis", "2014");

        line_chart_dataset.addValue(16, "mybatis-plus", "1970");
        line_chart_dataset.addValue(33, "mybatis-plus", "1980");
        line_chart_dataset.addValue(65, "mybatis-plus", "1990");
        line_chart_dataset.addValue(125, "mybatis-plus", "2000");
        line_chart_dataset.addValue(260, "mybatis-plus", "2010");
        line_chart_dataset.addValue(370, "mybatis-plus", "2014");

        line_chart_dataset.addValue(12, "nway", "1970");
        line_chart_dataset.addValue(27, "nway", "1980");
        line_chart_dataset.addValue(55, "nway", "1990");
        line_chart_dataset.addValue(119, "nway", "2000");
        line_chart_dataset.addValue(230, "nway", "2010");
        line_chart_dataset.addValue(250, "nway", "2014");

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Schools Vs Years", "Year",
                "Schools Count",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 640; /* Width of the image */
        int height = 480; /* Height of the image */
        File lineChart = new File("LineChart.jpeg");
        ChartUtils.saveChartAsJPEG(lineChart, lineChartObject, width, height);//原文出自【易百教程】，商业转载请联系作者获得授权，非商业请保留原文链接：https://www.yiibai.com/jfreechart/jfreechart_line_chart.html
        System.out.println(lineChart.getAbsolutePath());
    }

    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(15, "schools", "1970");
        dataset.addValue(30, "schools", "1980");
        dataset.addValue(60, "schools", "1990");
        dataset.addValue(120, "schools", "2000");
        dataset.addValue(240, "schools", "2010");
        dataset.addValue(300, "schools", "2014");
        return dataset;
    }
}
