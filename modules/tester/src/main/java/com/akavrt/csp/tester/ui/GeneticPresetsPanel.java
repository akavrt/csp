package com.akavrt.csp.tester.ui;

import com.akavrt.csp.solver.genetic.GeneticAlgorithmParameters;
import com.akavrt.csp.tester.ui.utils.GBC;
import com.akavrt.csp.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 15:50
 */
public class GeneticPresetsPanel extends BasePresetsPanel {
    private JFormattedTextField populationSizeEdit;
    private JFormattedTextField exchangeSizeEdit;
    private JFormattedTextField crossoverRateEdit;
    private JFormattedTextField runStepsEdit;

    public GeneticPresetsPanel() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(4);

        populationSizeEdit = createIntValueEdit();
        exchangeSizeEdit = createIntValueEdit();
        crossoverRateEdit = createFloatValueEdit(nf);
        runStepsEdit = createIntValueEdit();

        setLayout(new GridBagLayout());
        add(new JLabel("Genetic algorithm"),
            new GBC(0, 0, 2, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(10, 10, 10, 10));

        add(new JLabel("population size:", SwingConstants.RIGHT),
            new GBC(0, 1).setFill(GBC.BOTH).setWeight(100, 0).setInsets(5, 10, 3, 5));
        add(populationSizeEdit, new GBC(1, 1).setInsets(5, 0, 3, 10));

        add(new JLabel("exchange size:", SwingConstants.RIGHT),
            new GBC(0, 2).setFill(GBC.BOTH).setWeight(100, 0).setInsets(3, 10, 3, 5));
        add(exchangeSizeEdit, new GBC(1, 2).setInsets(3, 0, 3, 10));

        add(new JLabel("crossover rate:", SwingConstants.RIGHT),
            new GBC(0, 3).setFill(GBC.BOTH).setWeight(100, 0).setInsets(3, 10, 3, 5));
        add(crossoverRateEdit, new GBC(1, 3).setInsets(3, 0, 3, 10));

        add(new JLabel("run steps:", SwingConstants.RIGHT),
            new GBC(0, 4).setFill(GBC.BOTH).setWeight(100, 0).setInsets(3, 10, 10, 5));
        add(runStepsEdit, new GBC(1, 4).setInsets(3, 0, 10, 10));

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    }

    public GeneticAlgorithmParameters getParameters() {
        if (Utils.isEmpty(populationSizeEdit.getText())
                || Utils.isEmpty(exchangeSizeEdit.getText())
                || Utils.isEmpty(crossoverRateEdit.getText())
                || Utils.isEmpty(runStepsEdit.getText())) {
            return null;
        }

        int populationSize = ((Number) populationSizeEdit.getValue()).intValue();
        int exchangeSize = ((Number) exchangeSizeEdit.getValue()).intValue();
        double crossoverRate = ((Number) crossoverRateEdit.getValue()).doubleValue();
        int runSteps = ((Number) runStepsEdit.getValue()).intValue();

        GeneticAlgorithmParameters params = new GeneticAlgorithmParameters();
        params.setPopulationSize(populationSize);
        params.setExchangeSize(exchangeSize);
        params.setCrossoverRate(crossoverRate);
        params.setRunSteps(runSteps);

        return params;
    }

    public void setParameters(GeneticAlgorithmParameters params) {
        if (params == null) {
            populationSizeEdit.setText("");
            exchangeSizeEdit.setText("");
            crossoverRateEdit.setText("");
            runStepsEdit.setText("");
        } else {
            populationSizeEdit.setValue(params.getPopulationSize());
            exchangeSizeEdit.setValue(params.getExchangeSize());
            crossoverRateEdit.setValue(params.getCrossoverRate());
            runStepsEdit.setValue(params.getRunSteps());
        }
    }

}
