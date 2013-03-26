package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Plan;

/**
 * <p>This metric corresponds to the pattern reduction objective.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class PatternReductionMetric extends MinimizationMetric {

    /**
     * <p>Calculates pattern reduction ratio.</p>
     *
     * <p>If only one roll is used, then the fixed value of zero will be returned. Otherwise
     * evaluated value may vary from 0 to 1. The less is better.</p>
     *
     * @param plan The evaluated solution.
     * @return Pattern reduction fractional ratio.
     */
    @Override
    public double evaluate(Plan plan) {
        double ratio = 0;

        int activePatternsCount = plan.getMetricProvider().getActivePatternsCount();
        if (activePatternsCount > 1) {
            ratio = (plan.getMetricProvider().getUniquePatternsCount() - 1)
                    / (double) (activePatternsCount - 1);
        }

        return ratio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "PR";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Pattern reduction fractional ratio";
    }

}
