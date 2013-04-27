package com.akavrt.csp.tester.ui.presets;

import com.akavrt.csp.metrics.complex.ScalarMetricParameters;
import com.akavrt.csp.tester.ui.utils.GBC;
import com.akavrt.csp.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 18:26
 */
public class ScalarMetricPresetsPanel extends BasePresetsPanel {
    private JFormattedTextField trimFactorEdit;
    private JFormattedTextField patternsFactorEdit;
    private JFormattedTextField productionFactorEdit;

    public ScalarMetricPresetsPanel() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(4);

        trimFactorEdit = createFloatValueEdit(nf);
        patternsFactorEdit = createFloatValueEdit(nf);
        productionFactorEdit = createFloatValueEdit(nf);

        setLayout(new GridBagLayout());
        add(new JLabel("Objective function"),
            new GBC(0, 0, 2, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(10, 10, 10, 10));

        add(new JLabel("trim factor:", SwingConstants.RIGHT),
            new GBC(0, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(5, 10, 3, 5));
        add(trimFactorEdit, new GBC(1, 1).setInsets(5, 0, 3, 10));

        add(new JLabel("patterns factor:", SwingConstants.RIGHT),
            new GBC(0, 2).setFill(GBC.BOTH).setWeight(100, 0).setInsets(3, 10, 3, 5));
        add(patternsFactorEdit, new GBC(1, 2).setInsets(3, 0, 3, 10));

        add(new JLabel("production factor:", SwingConstants.RIGHT),
            new GBC(0, 3).setFill(GBC.BOTH).setWeight(100, 0).setInsets(3, 10, 10, 5));
        add(productionFactorEdit, new GBC(1, 3).setInsets(3, 0, 10, 10));
    }

    public ScalarMetricParameters getParameters() {
        if (Utils.isEmpty(trimFactorEdit.getText())
                || Utils.isEmpty(patternsFactorEdit.getText())
                || Utils.isEmpty(productionFactorEdit.getText())) {
            return null;
        }

        double trimFactor = ((Number) trimFactorEdit.getValue()).doubleValue();
        double patternsFactor = ((Number) patternsFactorEdit.getValue()).doubleValue();
        double productionFactor = ((Number) productionFactorEdit.getValue()).doubleValue();

        ScalarMetricParameters params = new ScalarMetricParameters();
        params.setTrimFactor(trimFactor);
        params.setPatternsFactor(patternsFactor);
        params.setProductionFactor(productionFactor);

        return params;
    }

    public void setParameters(ScalarMetricParameters params) {
        if (params == null) {
            trimFactorEdit.setText("");
            patternsFactorEdit.setText("");
            productionFactorEdit.setText("");
        } else {
            trimFactorEdit.setValue(params.getTrimFactor());
            patternsFactorEdit.setValue(params.getPatternsFactor());
            productionFactorEdit.setValue(params.getProductionFactor());
        }
    }

}
