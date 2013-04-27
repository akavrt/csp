package com.akavrt.csp.tester.ui.presets;

import com.akavrt.csp.metrics.complex.ConstraintAwareMetricParameters;
import com.akavrt.csp.tester.ui.utils.GBC;
import com.akavrt.csp.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 * User: akavrt
 * Date: 27.04.13
 * Time: 14:16
 */
public class ConstraintMetricPresetsPanel extends BasePresetsPanel {
    private JFormattedTextField trimFactorEdit;
    private JFormattedTextField patternsFactorEdit;

    public ConstraintMetricPresetsPanel() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(4);

        trimFactorEdit = createFloatValueEdit(nf);
        patternsFactorEdit = createFloatValueEdit(nf);

        setLayout(new GridBagLayout());
        add(new JLabel("Objective function"),
            new GBC(0, 0, 2, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(10, 10, 10, 10));

        add(new JLabel("trim factor:", SwingConstants.RIGHT),
            new GBC(0, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(5, 10, 3, 5));
        add(trimFactorEdit, new GBC(1, 1).setInsets(5, 0, 3, 10));

        add(new JLabel("patterns factor:", SwingConstants.RIGHT),
            new GBC(0, 2).setFill(GBC.BOTH).setWeight(100, 0).setInsets(3, 10, 3, 5));
        add(patternsFactorEdit, new GBC(1, 2).setInsets(3, 0, 3, 10));
    }

    public ConstraintAwareMetricParameters getParameters() {
        if (Utils.isEmpty(trimFactorEdit.getText())
                || Utils.isEmpty(patternsFactorEdit.getText())) {
            return null;
        }

        double trimFactor = ((Number) trimFactorEdit.getValue()).doubleValue();
        double patternsFactor = ((Number) patternsFactorEdit.getValue()).doubleValue();

        ConstraintAwareMetricParameters params = new ConstraintAwareMetricParameters();
        params.setAggregatedTrimFactor(trimFactor);
        params.setPatternsFactor(patternsFactor);

        return params;
    }

    public void setParameters(ConstraintAwareMetricParameters params) {
        if (params == null) {
            trimFactorEdit.setText("");
            patternsFactorEdit.setText("");
        } else {
            trimFactorEdit.setValue(params.getAggregatedTrimFactor());
            patternsFactorEdit.setValue(params.getPatternsFactor());
        }
    }

}
