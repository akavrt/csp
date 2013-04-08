package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Plan;

/**
 * User: akavrt
 * Date: 08.04.13
 * Time: 20:55
 */
public class MaxOverProductionMetric extends MinimizationMetric {

    @Override
    public double evaluate(Plan plan) {
        return plan.getMetricProvider().getMaximumOverProductionRatio();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "MaxOvPD";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Maximum over production ratio";
    }

}
