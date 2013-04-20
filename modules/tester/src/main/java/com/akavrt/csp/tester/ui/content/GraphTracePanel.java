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
import java.awt.geom.Ellipse2D;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 19:12
 */
public class GraphTracePanel extends JPanel {
    // trim loss
    private XYSeries trimBestSeries;
    private XYSeries trimAverageSeries;
    private XYSeries trimTradeoffSeries;
    // pattern reduction
    private XYSeries patternsBestSeries;
    private XYSeries patternsAverageSeries;
    private XYSeries patternsTradeoffUniqueSeries;
    private XYSeries patternsTradeoffTotalSeries;
    // product deviation
    private XYSeries productionBestSeries;
    private XYSeries productionAverageSeries;
    private XYSeries productionTradeoffSeries;
    private XYSeries productionTradeoffMaxUnderProdSeries;
    private XYSeries productionTradeoffMaxOverProdSeries;
    // scalar metric (aggregated)
    private XYSeries scalarBestSeries;
    private XYSeries comparativeAverageSeries;
    private XYSeries comparativeBestSeries;

    public GraphTracePanel() {
        setupViews();

        setBackground(Color.white);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    }

    public void clearSeries() {
        trimBestSeries.clear();
        trimAverageSeries.clear();
        trimTradeoffSeries.clear();

        patternsBestSeries.clear();
        patternsAverageSeries.clear();
        patternsTradeoffUniqueSeries.clear();
        patternsTradeoffTotalSeries.clear();

        productionBestSeries.clear();
        productionAverageSeries.clear();
        productionTradeoffSeries.clear();
        productionTradeoffMaxUnderProdSeries.clear();
        productionTradeoffMaxOverProdSeries.clear();

        scalarBestSeries.clear();
        comparativeAverageSeries.clear();
        comparativeBestSeries.clear();
    }

    public void updateSeries(SeriesData data) {
        if (data == null) {
            return;
        }

        trimBestSeries.add(data.age, 100 * data.trimBest);
        trimAverageSeries.add(data.age, 100 * data.trimAverage);
        trimTradeoffSeries.add(data.age, 100 * data.trimTradeoff);

        patternsBestSeries.add(data.age, data.patternsBest);
        patternsAverageSeries.add(data.age, data.patternsAverage);
        patternsTradeoffUniqueSeries.add(data.age, data.patternsTradeoffUnique);
        patternsTradeoffTotalSeries.add(data.age, data.patternsTradeoffTotal);

        productionBestSeries.add(data.age, 100 * data.productionBest);
        productionAverageSeries.add(data.age, 100 * data.productionAverage);
        productionTradeoffSeries.add(data.age, 100 * data.productionTradeoff);
        productionTradeoffMaxUnderProdSeries.add(data.age,
                                                 -100 * data.productionTradeoffMaxUnderProd);
        productionTradeoffMaxOverProdSeries.add(data.age, 100 * data.productionTradeoffMaxOverProd);

        scalarBestSeries.add(data.age, data.scalarBest);
        comparativeAverageSeries.add(data.age, data.comparativeAverage);
        comparativeBestSeries.add(data.age, data.comparativeBest);
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
        trimBestSeries = new XYSeries("P.LB");
        trimAverageSeries = new XYSeries("P.AV");
        trimTradeoffSeries = new XYSeries("B.TL");

        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(trimBestSeries);
        dataset.addSeries(trimAverageSeries);
        dataset.addSeries(trimTradeoffSeries);

        JFreeChart chart = createChart(dataset);

        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesShape(2, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));

        renderer.setSeriesPaint(0, new Color(0, 204, 51)); // best, green
        renderer.setSeriesPaint(1, Color.orange); // average, yellow
        renderer.setSeriesPaint(2, Color.red); // tradeoff, red

        return chart;
    }

    private JFreeChart createPatternsChart() {
        patternsBestSeries = new XYSeries("P.LB");
        patternsAverageSeries = new XYSeries("P.AV");
        patternsTradeoffUniqueSeries = new XYSeries("B.UP");
        patternsTradeoffTotalSeries = new XYSeries("B.TP");

        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(patternsBestSeries);
        dataset.addSeries(patternsAverageSeries);
        dataset.addSeries(patternsTradeoffUniqueSeries);
        dataset.addSeries(patternsTradeoffTotalSeries);

        JFreeChart chart = createChart(dataset);

        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesShape(2, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));

        renderer.setSeriesPaint(0, new Color(0, 204, 51)); // best, green
        renderer.setSeriesPaint(1, Color.orange); // average, yellow
        renderer.setSeriesPaint(2, Color.red); // tradeoff (unique patterns count), red
        renderer.setSeriesPaint(3, Color.blue); // tradeoff (total patterns count), blue

        return chart;
    }

    private JFreeChart createProductionChart() {
        productionBestSeries = new XYSeries("P.LB");
        productionAverageSeries = new XYSeries("P.AV");
        productionTradeoffSeries = new XYSeries("B.PD");
        productionTradeoffMaxOverProdSeries = new XYSeries("B.MOP");
        productionTradeoffMaxUnderProdSeries = new XYSeries("B.MUP");

        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(productionBestSeries);
        dataset.addSeries(productionAverageSeries);
        dataset.addSeries(productionTradeoffSeries);
        dataset.addSeries(productionTradeoffMaxOverProdSeries);
        dataset.addSeries(productionTradeoffMaxUnderProdSeries);

        JFreeChart chart = createChart(dataset);

        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesShape(2, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));

        renderer.setSeriesPaint(0, new Color(0, 204, 51)); // best, green
        renderer.setSeriesPaint(1, Color.orange); // average, yellow
        renderer.setSeriesPaint(2, Color.red); // tradeoff, red
        renderer.setSeriesPaint(3, Color.magenta); // tradeoff (max overproduction), magenta
        renderer.setSeriesPaint(4, Color.blue); // tradeoff (max underproduction), blue

        return chart;
    }

    private JFreeChart createScalarChart() {
        scalarBestSeries = new XYSeries("B.SCR");
        comparativeAverageSeries = new XYSeries("C.AV");
        comparativeBestSeries = new XYSeries("C.SCR");

        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(scalarBestSeries);
        dataset.addSeries(comparativeAverageSeries);
        dataset.addSeries(comparativeBestSeries);

        JFreeChart chart = createChart(dataset);

        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesShape(1, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));

        renderer.setSeriesPaint(0, new Color(0, 204, 51)); // best, green
        renderer.setSeriesPaint(1, Color.orange); // comparative average, yellow
        renderer.setSeriesPaint(2, Color.red); // comparative best, red

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
