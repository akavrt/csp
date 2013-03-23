package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Solution;

/**
 * <p>This metric corresponds to the pattern reduction objective.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class UniquePatternsMetric extends MinimizationMetric {

    /**
     * <p>Number of unique patterns used in a given solution.</p>
     *
     * <p>Acts as a mere wrapper.</p>
     *
     * @param solution The evaluated solution.
     * @return Number of unique patterns.
     */
    @Override
    public double evaluate(Solution solution) {
        return solution.getUniquePatternsCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "UP";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Unique patterns count";
    }

}
