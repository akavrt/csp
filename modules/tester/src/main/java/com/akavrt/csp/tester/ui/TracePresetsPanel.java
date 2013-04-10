package com.akavrt.csp.tester.ui;

import com.akavrt.csp.tester.ui.utils.GBC;

import javax.swing.*;
import java.awt.*;

/**
 * User: akavrt
 * Date: 10.04.13
 * Time: 18:37
 */
public class TracePresetsPanel extends JPanel {
    private JCheckBox useTextTraceCheck;
    private JCheckBox useGraphTraceCheck;

    public TracePresetsPanel() {
        this(false, false);
    }

    public TracePresetsPanel(boolean textTraceEnabled, boolean graphTraceEnabled) {
        useTextTraceCheck = new JCheckBox("text");
        useTextTraceCheck.setSelected(textTraceEnabled);

        useGraphTraceCheck = new JCheckBox("graph");
        useGraphTraceCheck.setSelected(graphTraceEnabled);

        setLayout(new GridBagLayout());
        add(new JLabel("General parameters"),
            new GBC(0, 0, 3, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(10, 10, 10, 10));

        add(new JLabel("Trace:", SwingConstants.RIGHT),
            new GBC(0, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(5, 10, 10, 5));
        add(useTextTraceCheck, new GBC(1, 1).setInsets(5, 0, 10, 0));
        add(useGraphTraceCheck, new GBC(2, 1).setInsets(5, 0, 10, 10));

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    }

    public boolean isTextTraceEnabled() {
        return useTextTraceCheck.isSelected();
    }

    public boolean isGraphTraceEnabled() {
        return useGraphTraceCheck.isSelected();
    }

    public void setTextTraceEnabled(boolean enabled) {
        useTextTraceCheck.setSelected(enabled);
    }

    public void setGraphTraceEnabled(boolean enabled) {
        useGraphTraceCheck.setSelected(enabled);
    }

}
