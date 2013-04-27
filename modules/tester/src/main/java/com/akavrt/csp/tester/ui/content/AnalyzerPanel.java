package com.akavrt.csp.tester.ui.content;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.tester.ui.SeriesMetricProvider;
import com.akavrt.csp.tester.ui.utils.GBC;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 19:13
 */
public class AnalyzerPanel extends JPanel {
    public static final double RANGE_CORRECTION = 1.3;
    public static final Integer DONE = 0;
    public static final Integer UNDER = 1;
    public static final Integer ORDER = 2;
    private final JTable table;
    private final JFreeChart chart;
    private final DefaultCategoryDataset dataset;
    private final SolutionsTableModel tableModel;
    private final ContentPanel.OnSolutionsSelectedListener listener;
    // solution details
    private JLabel aggregatedTrimRatioLabel;
    private JLabel sideTrimRatioLabel;
    private JLabel patternsRatioLabel;
    private JLabel patternsUniqueLabel;
    private JLabel patternsActiveLabel;
    private JLabel productDevRatioLabel;
    private JLabel productDevLowerLabel;
    private JLabel productDevUpperLabel;
    private Problem problem;
    private double chartRangeLowerBound;
    private double chartRangeUpperBound;

    public AnalyzerPanel(ContentPanel.OnSolutionsSelectedListener listener) {
        this.listener = listener;

        tableModel = new SolutionsTableModel();
        table = prepareTable(tableModel);

        dataset = new DefaultCategoryDataset();
        chart = createChart(dataset);

        chartRangeLowerBound = 0;
        chartRangeUpperBound = 200;
        setChartRange(chartRangeLowerBound, chartRangeUpperBound);

        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));

        JButton exportButton = new JButton("Export selected items to XML");
        exportButton.addActionListener(new ExportActionListener());
        exportButton.setFocusPainted(false);

        ChartPanel chartPanel = new ChartPanel(chart, true, true, false, true, false);
        chartPanel.setMinimumDrawHeight(150);

        JPanel detailsPanel = createDetailsPanel(chartPanel);

        setLayout(new GridBagLayout());
        add(new JLabel("Solutions"),
            new GBC(0, 0).setFill(GBC.BOTH).setWeight(100, 0).setAnchor(GBC.WEST)
                         .setInsets(10, 15, 0, 0));
        add(tablePane, new GBC(0, 1).setWeight(100, 100).setFill(GBC.BOTH).setInsets(10, 15, 0, 0));
        add(exportButton, new GBC(0, 2).setAnchor(GBC.CENTER).setInsets(10, 15, 10, 0));
        add(detailsPanel,
            new GBC(1, 0, 1, 3).setFill(GBC.BOTH).setWeight(100, 100).setInsets(10, 20, 15, 10));

        setBackground(Color.white);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    }

    public void setData(Problem problem, List<Solution> solutions,
                        SeriesMetricProvider metricProvider) {
        this.problem = problem;
        tableModel.setData(solutions, metricProvider);

        if (solutions != null && solutions.size() > 0) {
            adjustRange(solutions);
            table.setRowSelectionInterval(0, 0);
        }
    }

    public void clearData() {
        problem = null;
        tableModel.clearData();
        dataset.clear();

        chartRangeLowerBound = 0;
        chartRangeUpperBound = 200;
        setChartRange(chartRangeLowerBound, chartRangeUpperBound);

        clearDetails();
    }

    private void clearDetails() {
        aggregatedTrimRatioLabel.setText(" ");
        sideTrimRatioLabel.setText(" ");
        patternsRatioLabel.setText(" ");
        patternsUniqueLabel.setText(" ");
        patternsActiveLabel.setText(" ");
        productDevRatioLabel.setText(" ");
        productDevLowerLabel.setText(" ");
        productDevUpperLabel.setText(" ");
    }

    private JTable prepareTable(AbstractTableModel model) {
        JTable table = new JTable(model);

        TableColumnModel columnModel = table.getColumnModel();

        TableColumn selectionColumn = columnModel.getColumn(0);
        selectionColumn.setPreferredWidth(40);
        selectionColumn.setMinWidth(40);
        selectionColumn.setMaxWidth(60);
        selectionColumn.setResizable(false);

        TableColumn indexColumn = columnModel.getColumn(1);
        indexColumn.setCellRenderer(new IndexCellRenderer());
        indexColumn.setPreferredWidth(40);
        indexColumn.setMinWidth(40);
        indexColumn.setMaxWidth(60);
        indexColumn.setResizable(true);

        TableColumn comparativeRatioColumn = columnModel.getColumn(2);
        comparativeRatioColumn.setCellRenderer(new ScalarCellRenderer());
        comparativeRatioColumn.setMinWidth(40);
        comparativeRatioColumn.setResizable(true);

        TableColumn scalarRatioColumn = columnModel.getColumn(3);
        scalarRatioColumn.setCellRenderer(new ScalarCellRenderer());
        scalarRatioColumn.setMinWidth(40);
        scalarRatioColumn.setResizable(true);

        TableColumn feasibilityColumn = columnModel.getColumn(4);
        feasibilityColumn.setPreferredWidth(60);
        feasibilityColumn.setMinWidth(60);
        feasibilityColumn.setResizable(false);

        table.getSelectionModel().addListSelectionListener(new TableSelectionHandler());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Font headerFont = table.getTableHeader().getFont();
        headerFont = new Font(headerFont.getName(), Font.BOLD, headerFont.getSize());
        table.getTableHeader().setFont(headerFont);
        table.getTableHeader().setDefaultRenderer(new HeaderCellRenderer(table));

        table.setFillsViewportHeight(true);

        return table;
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createStackedBarChart("", // title
                                                              "", // x axis (domain) label
                                                              "", // y axis (range) label
                                                              dataset, // data
                                                              PlotOrientation.HORIZONTAL,
                                                              false, // include legend
                                                              true, // tooltips
                                                              false // urls
        );

        chart.setBackgroundPaint(Color.white);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        plot.setOutlineVisible(false);

        BarRenderer barRenderer = (BarRenderer) plot.getRenderer();
        barRenderer.setBarPainter(new StandardBarPainter());
        barRenderer.setBase(100.0);
        barRenderer.setShadowVisible(false);
        barRenderer.setDrawBarOutline(false);
        barRenderer.setIncludeBaseInRange(true);

        // change the auto tick unit selection to integer units only
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickLabelFont(createAxisFont());

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(createAxisFont());

        DecimalFormat df = new DecimalFormat("' +'0.0'%';-0.0'% '");
        CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", df);

        CategoryItemRenderer itemRenderer = plot.getRenderer();
        itemRenderer.setBaseItemLabelGenerator(generator);
        itemRenderer.setBaseItemLabelFont(createAxisFont());
        itemRenderer.setBaseItemLabelsVisible(true);
        itemRenderer.setBasePositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.HALF_ASCENT_LEFT));
        itemRenderer.setBaseNegativeItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE9, TextAnchor.HALF_ASCENT_RIGHT));

        return chart;
    }

    private Font createAxisFont() {
        return new Font(Font.SANS_SERIF, Font.PLAIN, 9);
    }

    private JPanel createDetailsPanel(ChartPanel chartPanel) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridBagLayout());

        detailsPanel.add(new JLabel("Production diagram"),
                         new GBC(0, 0, 4, 1).setFill(GBC.BOTH).setWeight(100, 0)
                                            .setAnchor(GBC.WEST));
        detailsPanel.add(chartPanel, new GBC(0, 1, 4, 1).setFill(GBC.BOTH).setWeight(100, 100));
        detailsPanel.add(new JLabel("Solution details"),
                         new GBC(0, 2, 4, 1).setFill(GBC.BOTH).setWeight(100, 0)
                                            .setAnchor(GBC.WEST).setInsets(10, 0, 0, 0));

        aggregatedTrimRatioLabel = new JLabel(" ");
        sideTrimRatioLabel = new JLabel(" ");
        detailsPanel.add(new JLabel("Material utilization"),
                         new GBC(0, 3).setAnchor(GBC.EAST).setInsets(10, 10, 0, 0));
        detailsPanel.add(aggregatedTrimRatioLabel,
                         new GBC(0, 4).setAnchor(GBC.EAST).setInsets(6, 10, 0, 6));
        detailsPanel.add(sideTrimRatioLabel,
                         new GBC(0, 5).setAnchor(GBC.EAST).setInsets(4, 10, 0, 6));

        int columnPadding = 40;
        patternsRatioLabel = new JLabel(" ");
        patternsUniqueLabel = new JLabel(" ");
        patternsActiveLabel = new JLabel(" ");
        detailsPanel.add(new JLabel("Pattern reduction"),
                         new GBC(1, 3).setAnchor(GBC.EAST).setInsets(10, columnPadding, 0, 0));
        detailsPanel.add(patternsRatioLabel,
                         new GBC(1, 4).setAnchor(GBC.EAST).setInsets(6, columnPadding, 0, 6));
        detailsPanel.add(patternsUniqueLabel,
                         new GBC(1, 5).setAnchor(GBC.EAST).setInsets(4, columnPadding, 0, 6));
        detailsPanel.add(patternsActiveLabel,
                         new GBC(1, 6).setAnchor(GBC.EAST).setInsets(4, columnPadding, 0, 6));

        productDevRatioLabel = new JLabel(" ");
        productDevLowerLabel = new JLabel(" ");
        productDevUpperLabel = new JLabel(" ");
        detailsPanel.add(new JLabel("Product deviation"),
                         new GBC(2, 3).setAnchor(GBC.EAST).setInsets(10, columnPadding, 0, 0));
        detailsPanel.add(productDevRatioLabel,
                         new GBC(2, 4).setAnchor(GBC.EAST).setInsets(6, columnPadding, 0, 6));
        detailsPanel.add(productDevLowerLabel,
                         new GBC(2, 5).setAnchor(GBC.EAST).setInsets(4, columnPadding, 0, 6));
        detailsPanel.add(productDevUpperLabel,
                         new GBC(2, 6).setAnchor(GBC.EAST).setInsets(4, columnPadding, 0, 6));

        detailsPanel.setBackground(Color.white);

        return detailsPanel;
    }

    private void adjustRange(List<Solution> solutions) {
        double maxDeviation = 0;
        for (Solution solution : solutions) {
            double underProduction = solution.getMetricProvider().getMaximumUnderProductionRatio();
            double overProduction = solution.getMetricProvider().getMaximumOverProductionRatio();

            if (maxDeviation < Math.max(underProduction, overProduction)) {
                maxDeviation = Math.max(underProduction, overProduction);
            }
        }

        if (maxDeviation == 0) {
            maxDeviation = 0.01;
        }

        chartRangeLowerBound = 100 * (1 - RANGE_CORRECTION * maxDeviation);
        chartRangeUpperBound = 100 * (1 + RANGE_CORRECTION * maxDeviation);
        setChartRange(chartRangeLowerBound, chartRangeUpperBound);
    }

    private void setChartRange(double lowerBound, double upperBound) {
        CategoryPlot solutionPlot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) solutionPlot.getRangeAxis();
        rangeAxis.setRange(lowerBound, upperBound);
    }

    private void visualizeProduction(Solution solution) {
        dataset.clear();

        if (problem == null) {
            return;
        }

        for (Order order : problem.getOrders()) {
            double required = order.getLength();
            double produced = solution.getProductionLengthForOrder(order);

            Integer status = produced > required ? ORDER : (produced < required ? UNDER : DONE);
            double ratio = produced / required - 1;

            dataset.addValue(100 * ratio, status, order.getId());
        }

        setSeriesColors();
    }

    private void setSeriesColors() {
        CategoryPlot solutionPlot = (CategoryPlot) chart.getPlot();
        BarRenderer solutionRenderer = (BarRenderer) solutionPlot.getRenderer();

        int underproductionSeriesIndex = dataset.getRowIndex(UNDER);
        if (underproductionSeriesIndex != -1) {
            // red
            solutionRenderer.setSeriesPaint(underproductionSeriesIndex, Color.red);
        }

        int overproductionSeriesIndex = dataset.getRowIndex(ORDER);
        if (overproductionSeriesIndex != -1) {
            // yellow
            solutionRenderer.setSeriesPaint(overproductionSeriesIndex, Color.orange);
        }

        int doneSeriesIndex = dataset.getRowIndex(DONE);
        if (doneSeriesIndex != -1) {
            // green
            solutionRenderer.setSeriesPaint(doneSeriesIndex, new Color(0, 204, 51));
        }
    }

    private void visualizeDetails(SolutionsTableModel.TableRowData details) {
        if (details == null) {
            clearDetails();
            return;
        }

        aggregatedTrimRatioLabel.setText(String.format("waste %.1f%%",
                                                       100 * details.aggregatedTrimRatio));
        sideTrimRatioLabel.setText(String.format("trim %.1f%%", 100 * details.trimRatio));
        patternsRatioLabel.setText(String.format("%.1f%%", 100 * details.patternsRatio));
        patternsUniqueLabel.setText(String.format("%d unique", details.uniquePatterns));
        patternsActiveLabel.setText(String.format("%d total", details.totalPatterns));
        productDevRatioLabel.setText(String.format("%.1f%%", 100 * details.productionRatio));
        productDevLowerLabel.setText(String.format("from %+.1f%%",
                                                   -100 * details.maxUnderProductionRatio));
        productDevUpperLabel.setText(String.format("to %+.1f%%",
                                                   100 * details.maxOverProductionRatio));
    }

    private class TableSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            if (!e.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
                int selection = lsm.getMinSelectionIndex();

                Solution selectedSolution = tableModel.getSolution(selection);
                if (selectedSolution != null) {
                    visualizeProduction(selectedSolution);

                    // restore chart range
                    setChartRange(chartRangeLowerBound, chartRangeUpperBound);
                }

                SolutionsTableModel.TableRowData rowData = tableModel.getDetails(selection);
                if (rowData != null) {
                    visualizeDetails(rowData);
                }
            }
        }
    }

    private class ExportActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int selection = table.getSelectedRow();

            if (listener != null) {
                List<Solution> selectedSolutions = tableModel.getSelected();
                listener.onSolutionsSelected(selectedSolutions);
            }

            tableModel.clearSelection();

            if (selection >= 0) {
                table.setRowSelectionInterval(selection, selection);
            }
        }
    }

}