package com.akavrt.csp.metrics.simple;

import com.akavrt.csp.core.Plan;
import com.akavrt.csp.metrics.MinimizationMetric;

/**
 * <p>This metric corresponds to the trim loss minimization objective.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class TrimLossMetric extends MinimizationMetric {
    /**
     * <p>Calculate trim loss ratio.</p>
     *
     * <p>Evaluated value may vary from 0 to 1. The less is better.<p/>
     *
     * @param plan The evaluated solution.
     * @return Trim loss fractional ratio.
     */
    @Override
    public double evaluate(Plan plan) {
        return plan.getMetricProvider().getTrimRatio();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "TL";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Trim loss fractional ratio";
    }

}
