package com.akavrt.csp.tester.ui.content;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * User: akavrt
 * Date: 11.04.13
 * Time: 21:23
 */
public class ScalarCellRenderer extends DefaultTableCellRenderer {
    private final DecimalFormat formatter;

    public ScalarCellRenderer() {
        formatter = new DecimalFormat("0.000");
    }

    public void setValue(Object value) {
        setText(value == null ? "" : formatter.format(value));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setHorizontalAlignment(SwingConstants.CENTER);

        return this;
    }
}
