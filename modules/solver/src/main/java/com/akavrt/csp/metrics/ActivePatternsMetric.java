package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Plan;

/**
 * <p>This metric corresponds to the pattern reduction objective.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ActivePatternsMetric extends MinimizationMetric{

    /**
     * <p>Number of active patterns used in a given solution.</p>
     *
     * <p>Acts as a mere wrapper.</p>
     *
     * @param plan The evaluated solution.
     * @return Number of active patterns.
     */
    @Override
    public double evaluate(Plan plan) {
        return plan.getActivePatternsCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "AP";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Active patterns count";
    }

}
