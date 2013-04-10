package com.akavrt.csp.tester.ui;

import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.tester.ui.utils.GBC;
import com.akavrt.csp.utils.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 15:22
 */
public class PatternPresetsPanel extends BasePresetsPanel {
    private JFormattedTextField trialsEdit;

    public PatternPresetsPanel() {
        trialsEdit = createIntValueEdit();

        setLayout(new GridBagLayout());
        add(new JLabel("Pattern generation"),
            new GBC(0, 0, 2, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(10, 10, 10, 10));

        add(new JLabel("max trials:", SwingConstants.RIGHT),
            new GBC(0, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(5, 10, 10, 5));
        add(trialsEdit, new GBC(1, 1).setInsets(5, 0, 10, 10));

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    }

    public PatternGeneratorParameters getParameters() {
        if (Utils.isEmpty(trialsEdit.getText())) {
            return null;
        }

        int trialsLimit = ((Number) trialsEdit.getValue()).intValue();

        PatternGeneratorParameters params = new PatternGeneratorParameters();
        params.setGenerationTrialsLimit(trialsLimit);

        return params;
    }

    public void setParameters(PatternGeneratorParameters params) {
        if (params == null) {
            trialsEdit.setText("");
        } else {
            trialsEdit.setValue(params.getGenerationTrialsLimit());
        }
    }
}
