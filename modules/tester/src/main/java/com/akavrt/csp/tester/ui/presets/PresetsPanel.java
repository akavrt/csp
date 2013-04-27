package com.akavrt.csp.tester.ui.presets;

import com.akavrt.csp.metrics.complex.ConstraintAwareMetricParameters;
import com.akavrt.csp.solver.evo.es.EvolutionStrategyParameters;
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
    private StrategyPresetsPanel strategyPanel;
    private ConstraintMetricPresetsPanel objectivePanel;

    public PresetsPanel() {
        setLayout(new GridBagLayout());

        // graph trace is enabled by default
        tracePanel = new TracePresetsPanel(false, true);
        patternPanel = new PatternPresetsPanel();
        strategyPanel = new StrategyPresetsPanel();
        objectivePanel = new ConstraintMetricPresetsPanel();

        add(tracePanel, new GBC(0, 0).setFill(GBC.HORIZONTAL).setWeight(100, 0));
        add(patternPanel, new GBC(0, 1).setFill(GBC.HORIZONTAL).setWeight(100, 0));
        add(strategyPanel, new GBC(0, 2).setFill(GBC.HORIZONTAL).setWeight(100, 0));
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

    public EvolutionStrategyParameters getEvolutionStrategyParameters() {
        return strategyPanel.getParameters();
    }

    public void setEvolutionStrategyParameters(EvolutionStrategyParameters parameters) {
        strategyPanel.setParameters(parameters);
    }

    public ConstraintAwareMetricParameters getObjectiveFunctionParameters() {
        return objectivePanel.getParameters();
    }

    public void setObjectiveFunctionParameters(ConstraintAwareMetricParameters parameters) {
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
