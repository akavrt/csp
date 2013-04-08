package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Plan;

/**
 * User: akavrt
 * Date: 08.04.13
 * Time: 20:17
 */
public class MaxUnderProductionMetric extends MinimizationMetric {

    @Override
    public double evaluate(Plan plan) {
        return plan.getMetricProvider().getMaximumUnderProductionRatio();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "MaxUnPD";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Maximum under production ratio";
    }

}