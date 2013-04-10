package com.akavrt.csp.tester.ui;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 17:40
 */
public class BasePresetsPanel extends JPanel {
    private static final int EDIT_WIDTH = 60;

    protected JFormattedTextField createIntValueEdit() {
        JFormattedTextField edit = new JFormattedTextField(NumberFormat.getIntegerInstance());
        edit.setHorizontalAlignment(JTextField.RIGHT);
        int height = edit.getPreferredSize().height;
        edit.setPreferredSize(new Dimension(EDIT_WIDTH, height));
        edit.setMinimumSize(new Dimension(EDIT_WIDTH, height));

        return edit;
    }

    protected JFormattedTextField createFloatValueEdit(NumberFormat nf) {
        JFormattedTextField edit = new JFormattedTextField(nf);
        edit.setHorizontalAlignment(JTextField.RIGHT);
        int height = edit.getPreferredSize().height;
        edit.setPreferredSize(new Dimension(EDIT_WIDTH, height));
        edit.setMinimumSize(new Dimension(EDIT_WIDTH, height));

        return edit;
    }

}
