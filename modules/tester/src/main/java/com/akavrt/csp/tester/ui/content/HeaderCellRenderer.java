package com.akavrt.csp.tester.ui.content;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * User: akavrt
 * Date: 11.04.13
 * Time: 21:25
 */
public class HeaderCellRenderer implements TableCellRenderer {
    DefaultTableCellRenderer renderer;

    public HeaderCellRenderer(JTable table) {
        renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int col) {
        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                                      row, col);
    }
}

