package com.akavrt.csp.tester.ui.content;

import com.akavrt.csp.core.Solution;
import com.akavrt.csp.tester.ui.SeriesMetricProvider;
import com.google.common.collect.Lists;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * User: akavrt
 * Date: 11.04.13
 * Time: 17:52
 */
public class SolutionsTableModel extends AbstractTableModel {
    private static final int SELECTION_INDEX = 0;
    private static final int NUMBER_INDEX = 1;
    private static final int COMPARATIVE_INDEX = 2;
    private static final int SCALAR_INDEX = 3;
    private static final int FEASIBILITY_INDEX = 4;
    private final String[] columnNames = {"", "#", "3-scalar", "2-scalar", "feasible"};
    private final List<TableRowData> rowData;
    private final List<Solution> solutions;

    public SolutionsTableModel() {
        rowData = Lists.newArrayList();
        solutions = Lists.newArrayList();
    }

    public void setData(List<Solution> data, SeriesMetricProvider metricProvider) {
        solutions.clear();
        rowData.clear();

        if (data != null && data.size() > 0) {
            solutions.addAll(data);

            for (Solution solution : data) {
                rowData.add(new TableRowData(solution, metricProvider));
            }
        }

        fireTableDataChanged();
    }

    public void clearData() {
        solutions.clear();
        rowData.clear();

        fireTableDataChanged();
    }

    public Solution getSolution(int index) {
        if (index < 0 || index > solutions.size() - 1) {
            return null;
        }

        return solutions.get(index);
    }

    public TableRowData getDetails(int index) {
        if (index < 0 || index > rowData.size() - 1) {
            return null;
        }

        return rowData.get(index);
    }

    public void clearSelection() {
        for (TableRowData data : rowData) {
            data.setSelected(false);
        }

        fireTableDataChanged();
    }

    public List<Solution> getSelected() {
        List<Solution> selection = Lists.newArrayList();

        for (int i = 0; i < rowData.size(); i++) {
            if (rowData.get(i).isSelected()) {
                selection.add(solutions.get(i));
            }
        }

        return selection;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return rowData.size();
    }

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object holder = null;

        switch (columnIndex) {
            case SELECTION_INDEX:
                holder = rowData.get(rowIndex).isSelected();
                break;

            case NUMBER_INDEX:
                holder = rowIndex + 1;
                break;

            case COMPARATIVE_INDEX:
                holder = rowData.get(rowIndex).comparativeRatio;
                break;

            case SCALAR_INDEX:
                holder = rowData.get(rowIndex).scalarRatio;
                break;

            case FEASIBILITY_INDEX:
                holder = rowData.get(rowIndex).isFeasible;
                break;
        }

        return holder;
    }

    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == SELECTION_INDEX;
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == SELECTION_INDEX) {
            rowData.get(rowIndex).setSelected((Boolean) value);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public static class TableRowData {
        public final double trimRatio;
        public final double patternsRatio;
        public final int uniquePatterns;
        public final int totalPatterns;
        public final double productionRatio;
        public final double maxUnderProductionRatio;
        public final double maxOverProductionRatio;
        public final double scalarRatio;
        public final double aggregatedTrimRatio;
        public final double comparativeRatio;
        public final boolean isFeasible;
        private boolean isSelected;

        public TableRowData(Solution solution, SeriesMetricProvider metricProvider) {
            trimRatio = metricProvider.getTrimMetric().evaluate(solution);

            patternsRatio = metricProvider.getPatternsMetric().evaluate(solution);
            uniquePatterns = solution.getMetricProvider().getUniquePatternsCount();
            totalPatterns = solution.getMetricProvider().getActivePatternsCount();

            productionRatio = metricProvider.getProductMetric().evaluate(solution);
            maxUnderProductionRatio = solution.getMetricProvider().getMaximumUnderProductionRatio();
            maxOverProductionRatio = solution.getMetricProvider().getMaximumOverProductionRatio();

            scalarRatio = metricProvider.getScalarMetric().evaluate(solution);
            aggregatedTrimRatio = solution.getMetricProvider().getAggregatedTrimRatio();
            comparativeRatio = metricProvider.getComparativeMetric().evaluate(solution);

            isFeasible = solution.isFeasible();
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            this.isSelected = selected;
        }
    }
}
