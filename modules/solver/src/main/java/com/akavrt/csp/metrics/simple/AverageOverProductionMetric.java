package com.akavrt.csp.metrics.simple;

import com.akavrt.csp.core.Plan;
import com.akavrt.csp.metrics.MinimizationMetric;

/**
 * User: akavrt
 * Date: 08.04.13
 * Time: 19:47
 */
public class AverageOverProductionMetric extends MinimizationMetric {

    @Override
    public double evaluate(Plan plan) {
        return plan.getMetricProvider().getAverageOverProductionRatio();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "OvPD";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Average over production ratio";
    }

}
