package com.akavrt.csp.tester.ui;

import com.akavrt.csp.metrics.ScalarMetricParameters;
import com.akavrt.csp.solver.genetic.GeneticAlgorithmParameters;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.tester.ui.utils.GBC;

import javax.swing.*;
import java.awt.*;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 14:48
 */
public class PresetsPanel extends JPanel {
    private TracePresetsPanel tracePanel;
    private PatternPresetsPanel patternPanel;
    private GeneticPresetsPanel geneticPanel;
    private ObjectivePresetsPanel objectivePanel;

    public PresetsPanel() {
        setLayout(new GridBagLayout());

        // graph trace is enabled by default
        tracePanel = new TracePresetsPanel(false, true);
        patternPanel = new PatternPresetsPanel();
        geneticPanel = new GeneticPresetsPanel();
        objectivePanel = new ObjectivePresetsPanel();

        add(tracePanel, new GBC(0, 0).setFill(GBC.HORIZONTAL).setWeight(100, 0));
        add(patternPanel, new GBC(0, 1).setFill(GBC.HORIZONTAL).setWeight(100, 0));
        add(geneticPanel, new GBC(0, 2).setFill(GBC.HORIZONTAL).setWeight(100, 0));
        add(objectivePanel, new GBC(0, 3).setFill(GBC.HORIZONTAL).setWeight(100, 0));
        add(new JPanel(), new GBC(0, 4).setFill(GBC.BOTH).setWeight(100, 100));

        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, Color.GRAY));
    }

    public PatternGeneratorParameters getPatternGeneratorParameters() {
        return patternPanel.getParameters();
    }

    public void setPatternGeneratorParameters(PatternGeneratorParameters parameters) {
        patternPanel.setParameters(parameters);
    }

    public GeneticAlgorithmParameters getGeneticAlgorithmParameters() {
        return geneticPanel.getParameters();
    }

    public void setGeneticAlgorithmParameters(GeneticAlgorithmParameters parameters) {
        geneticPanel.setParameters(parameters);
    }

    public ScalarMetricParameters getObjectiveFunctionParameters() {
        return objectivePanel.getParameters();
    }

    public void setObjectiveFunctionParameters(ScalarMetricParameters parameters) {
        objectivePanel.setParameters(parameters);
    }

    public boolean isTextTraceEnabled() {
        return tracePanel.isTextTraceEnabled();
    }

    public boolean isGraphTraceEnabled() {
        return tracePanel.isGraphTraceEnabled();
    }

    public void setTextTraceEnabled(boolean enabled) {
        tracePanel.setTextTraceEnabled(enabled);
    }

    public void setGraphTraceEnabled(boolean enabled) {
        tracePanel.setGraphTraceEnabled(enabled);
    }

}
