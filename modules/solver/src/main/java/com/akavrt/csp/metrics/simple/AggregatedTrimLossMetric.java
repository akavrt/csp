package com.akavrt.csp.metrics.simple;

import com.akavrt.csp.core.Plan;
import com.akavrt.csp.metrics.MinimizationMetric;

/**
 * User: akavrt
 * Date: 27.04.13
 * Time: 14:44
 */
public class AggregatedTrimLossMetric extends MinimizationMetric {
    /**
     * <p>Calculate aggregated trim loss ratio. Excessive amount of produced strip is treated as
     * trim.</p>
     *
     * <p>Evaluated value may vary from 0 to 1. The less is better.<p/>
     *
     * @param plan The evaluated solution.
     * @return Aggregated trim loss fractional ratio.
     */
    @Override
    public double evaluate(Plan plan) {
        return plan.getMetricProvider().getAggregatedTrimRatio();
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
