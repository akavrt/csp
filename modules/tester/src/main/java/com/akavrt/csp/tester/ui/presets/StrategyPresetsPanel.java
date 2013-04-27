package com.akavrt.csp.tester.ui.presets;

import com.akavrt.csp.solver.evo.es.EvolutionStrategyParameters;
import com.akavrt.csp.tester.ui.utils.GBC;
import com.akavrt.csp.utils.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * User: akavrt
 * Date: 27.04.13
 * Time: 14:06
 */
public class StrategyPresetsPanel extends BasePresetsPanel {
    private JFormattedTextField populationSizeEdit;
    private JFormattedTextField exchangeSizeEdit;
    private JFormattedTextField runStepsEdit;

    public StrategyPresetsPanel() {
        populationSizeEdit = createIntValueEdit();
        exchangeSizeEdit = createIntValueEdit();
        runStepsEdit = createIntValueEdit();

        setLayout(new GridBagLayout());
        add(new JLabel("Evolution strategy"),
            new GBC(0, 0, 2, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(10, 10, 10, 10));

        add(new JLabel("population size:", SwingConstants.RIGHT),
            new GBC(0, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(5, 10, 3, 5));
        add(populationSizeEdit, new GBC(1, 1).setInsets(5, 0, 3, 10));

        add(new JLabel("exchange size:", SwingConstants.RIGHT),
            new GBC(0, 2).setFill(GBC.BOTH).setWeight(100, 0).setInsets(3, 10, 3, 5));
        add(exchangeSizeEdit, new GBC(1, 2).setInsets(3, 0, 3, 10));

        add(new JLabel("run steps:", SwingConstants.RIGHT),
            new GBC(0, 3).setFill(GBC.BOTH).setWeight(100, 0).setInsets(3, 10, 10, 5));
        add(runStepsEdit, new GBC(1, 3).setInsets(3, 0, 10, 10));

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    }

    public EvolutionStrategyParameters getParameters() {
        if (Utils.isEmpty(populationSizeEdit.getText())
                || Utils.isEmpty(exchangeSizeEdit.getText())
                || Utils.isEmpty(runStepsEdit.getText())) {
            return null;
        }

        int populationSize = ((Number) populationSizeEdit.getValue()).intValue();
        int exchangeSize = ((Number) exchangeSizeEdit.getValue()).intValue();
        int runSteps = ((Number) runStepsEdit.getValue()).intValue();

        EvolutionStrategyParameters params = new EvolutionStrategyParameters();
        params.setPopulationSize(populationSize);
        params.setOffspringCount(exchangeSize);
        params.setRunSteps(runSteps);

        return params;
    }

    public void setParameters(EvolutionStrategyParameters params) {
        if (params == null) {
            populationSizeEdit.setText("");
            exchangeSizeEdit.setText("");
            runStepsEdit.setText("");
        } else {
            populationSizeEdit.setValue(params.getPopulationSize());
            exchangeSizeEdit.setValue(params.getOffspringCount());
            runStepsEdit.setValue(params.getRunSteps());
        }
    }

}

