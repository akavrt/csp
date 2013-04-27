package com.akavrt.csp.tester.ui.content;

import com.akavrt.csp.tester.ui.SeriesData;
import com.akavrt.csp.tester.ui.utils.GBC;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 19:12
 */
public class GraphTracePanel extends JPanel {
    // trim loss
    private XYSeries tradeoffSideTrimRatioSeries;
    private XYSeries tradeoffTotalTrimRatioSeries;
    // pattern reduction
    private XYSeries partialBestPatternsRatioSeries;
    private XYSeries tradeoffUniquePatternsCountSeries;
    private XYSeries tradeoffTotalPatternsCountSeries;
    // product deviation
    private XYSeries tradeoffProductionRatioSeries;
    private XYSeries tradeoffMaxUnderProductionRatioSeries;
    private XYSeries tradeoffMaxOverProductionRatioSeries;
    // scalar metric (aggregated)
    private XYSeries tradeoffObjectiveRatioSeries;
    private XYSeries tradeoffComparativeRatioSeries;
    private XYSeries averageObjectiveRatioSeries;

    public GraphTracePanel() {
        setupViews();

        setBackground(Color.white);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    }

    public void clearSeries() {
        tradeoffSideTrimRatioSeries.clear();
        tradeoffTotalTrimRatioSeries.clear();

        partialBestPatternsRatioSeries.clear();
        tradeoffUniquePatternsCountSeries.clear();
        tradeoffTotalPatternsCountSeries.clear();

        tradeoffProductionRatioSeries.clear();
        tradeoffMaxUnderProductionRatioSeries.clear();
        tradeoffMaxOverProductionRatioSeries.clear();

        tradeoffObjectiveRatioSeries.clear();
        tradeoffComparativeRatioSeries.clear();
        averageObjectiveRatioSeries.clear();
    }

    public void updateSeries(SeriesData data) {
        if (data == null) {
            return;
        }

        tradeoffSideTrimRatioSeries.add(data.age, 100 * data.tradeoffSideTrimRatio);
        tradeoffTotalTrimRatioSeries.add(data.age, 100 * data.tradeoffTotalTrimRatio);

        partialBestPatternsRatioSeries.add(data.age, data.partialBestPatternsRatio);
        tradeoffUniquePatternsCountSeries.add(data.age, data.tradeoffUniquePatternsCount);
        tradeoffTotalPatternsCountSeries.add(data.age, data.tradeoffTotalPatternsCount);

        tradeoffProductionRatioSeries.add(data.age, 100 * data.tradeoffProductionRatio);
        tradeoffMaxUnderProductionRatioSeries.add(data.age,
                                                  -100 * data.tradeoffMaxUnderProductionRatio);
        tradeoffMaxOverProductionRatioSeries.add(data.age,
                                                 100 * data.tradeoffMaxOverProductionRatio);

        tradeoffObjectiveRatioSeries.add(data.age, data.tradeoffObjectiveRatio);
        tradeoffComparativeRatioSeries.add(data.age, data.tradeoffComparativeRatio);
        averageObjectiveRatioSeries.add(data.age, data.averageObjectiveRatio);
    }

    private void setupViews() {
        JFreeChart trimChart = createTrimChart();
        ChartPanel trimLossPanel = new ChartPanel(trimChart, true, true, false, true, false);
        trimLossPanel.setMinimumDrawHeight(80);

        JFreeChart patternsChart = createPatternsChart();
        ChartPanel patternsPanel = new ChartPanel(patternsChart, true, true, false, true, false);
        patternsPanel.setMinimumDrawHeight(80);

        JFreeChart productionChart = createProductionChart();
        ChartPanel productionPanel = new ChartPanel(productionChart, true, true, false, true,
                                                    false);
        productionPanel.setMinimumDrawHeight(80);

        JFreeChart scalarChart = createScalarChart();
        ChartPanel totalPanel = new ChartPanel(scalarChart, true, true, false, true, false);
        totalPanel.setMinimumDrawHeight(80);

        setLayout(new GridBagLayout());

        add(new JLabel("Trim loss, %"), new GBC(0, 0).setAnchor(GBC.WEST).setInsets(10, 10, 5, 0));
        add(trimLossPanel, new GBC(0, 1).setWeight(100, 100).setFill(GBC.BOTH));

        add(new JLabel("Pattern reduction"),
            new GBC(0, 2).setAnchor(GBC.WEST).setInsets(10, 10, 5, 0));
        add(patternsPanel, new GBC(0, 3).setWeight(100, 100).setFill(GBC.BOTH));

        add(new JLabel("Product deviation, %"),
            new GBC(1, 0).setAnchor(GBC.WEST).setInsets(10, 10, 5, 0));
        add(productionPanel, new GBC(1, 1).setWeight(100, 100).setFill(GBC.BOTH));

        add(new JLabel("Aggregate objective"),
            new GBC(1, 2).setAnchor(GBC.WEST).setInsets(10, 10, 5, 0));
        add(totalPanel, new GBC(1, 3).setWeight(100, 100).setFill(GBC.BOTH));
    }

    private JFreeChart createTrimChart() {
        tradeoffSideTrimRatioSeries = new XYSeries("SIDE");
        tradeoffTotalTrimRatioSeries = new XYSeries("TOTAL");

        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(tradeoffSideTrimRatioSeries);
        dataset.addSeries(tradeoffTotalTrimRatioSeries);

        JFreeChart chart = createChart(dataset);

        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, Color.blue); // side trim, blue
        renderer.setSeriesPaint(1, Color.red); // aggregated trim, red

        return chart;
    }

    private JFreeChart createPatternsChart() {
        partialBestPatternsRatioSeries = new XYSeries("LB");
        tradeoffUniquePatternsCountSeries = new XYSeries("UNIQUE");
        tradeoffTotalPatternsCountSeries = new XYSeries("TOTAL");

        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(partialBestPatternsRatioSeries);
        dataset.addSeries(tradeoffUniquePatternsCountSeries);
        dataset.addSeries(tradeoffTotalPatternsCountSeries);

        JFreeChart chart = createChart(dataset);

        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, new Color(0, 204, 51)); // lower bound, green
        renderer.setSeriesPaint(1, Color.red); // unique patterns count, red
        renderer.setSeriesPaint(2, Color.blue); // total patterns count, blue

        return chart;
    }

    private JFreeChart createProductionChart() {
        tradeoffProductionRatioSeries = new XYSeries("AVER");
        tradeoffMaxUnderProductionRatioSeries = new XYSeries("MAX UP");
        tradeoffMaxOverProductionRatioSeries = new XYSeries("MAX OP");

        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(tradeoffProductionRatioSeries);
        dataset.addSeries(tradeoffMaxUnderProductionRatioSeries);
        dataset.addSeries(tradeoffMaxOverProductionRatioSeries);

        JFreeChart chart = createChart(dataset);

        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, Color.red); // average, red
        renderer.setSeriesPaint(1, Color.blue); // max underproduction, blue
        renderer.setSeriesPaint(2, Color.magenta); // max overproduction, magenta

        return chart;
    }

    private JFreeChart createScalarChart() {
        tradeoffObjectiveRatioSeries = new XYSeries("BEST");
        averageObjectiveRatioSeries = new XYSeries("AVER");
        tradeoffComparativeRatioSeries = new XYSeries("COMP");

        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(tradeoffObjectiveRatioSeries);
        dataset.addSeries(averageObjectiveRatioSeries);
        dataset.addSeries(tradeoffComparativeRatioSeries);

        JFreeChart chart = createChart(dataset);

        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, Color.red); // best, red
        renderer.setSeriesPaint(1, Color.orange); // average, yellow
        renderer.setSeriesPaint(2, new Color(0, 204, 51)); // comparative best, green

        return chart;
    }

    private JFreeChart createChart(XYSeriesCollection dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart("", // title
                                                          "", // x axis (domain) label
                                                          "", // y axis (range) label
                                                          dataset, // data
                                                          PlotOrientation.VERTICAL,
                                                          true, // include legend
                                                          true, // tooltips
                                                          false // urls
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setOutlineVisible(false);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(createAxisFont());

        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setTickLabelFont(createAxisFont());
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setBaseFillPaint(Color.white);

        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesStroke(0, new BasicStroke(1.1f));
        }

        LegendTitle legend = chart.getLegend();
        legend.setFrame(BlockBorder.NONE);
        legend.setItemFont(createLegendFont());
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setMargin(0, 0, 0, 10);

        return chart;
    }

    private Font createAxisFont() {
        return new Font(Font.SANS_SERIF, Font.PLAIN, 9);
    }

    private Font createLegendFont() {
        return new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    }

}
