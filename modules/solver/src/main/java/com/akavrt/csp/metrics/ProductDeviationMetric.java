package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Plan;

/**
 * <p>This metric corresponds to the minimization of the both under and overproduction of the final
 * strip ordered.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ProductDeviationMetric extends MinimizationMetric {
    /**
     * <p>Calculates product deviation ratio.</p>
     *
     * <p>Evaluated value may vary from 0 to 1. The less is better: zero means that there is no
     * overproduction or underproduction.</p>
     *
     * @param plan The evaluated solution.
     * @return Product deviation fractional ratio.
     */
    @Override
    public double evaluate(Plan plan) {
        return plan.getMetricProvider().getAverageUnderProductionRatio() +
                plan.getMetricProvider().getAverageOverProductionRatio();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "PD";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Product deviation fractional ratio";
    }
}
