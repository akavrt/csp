package com.akavrt.csp.metrics.simple;

import com.akavrt.csp.core.Plan;
import com.akavrt.csp.metrics.MinimizationMetric;

/**
 * User: akavrt
 * Date: 08.04.13
 * Time: 19:45
 */
public class AverageUnderProductionMetric extends MinimizationMetric {

    @Override
    public double evaluate(Plan plan) {
        return plan.getMetricProvider().getAverageUnderProductionRatio();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "UnPD";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Average under production ratio";
    }

}
