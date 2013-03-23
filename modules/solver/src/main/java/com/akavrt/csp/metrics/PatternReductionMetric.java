package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Solution;

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
     * @param solution The evaluated solution.
     * @return Pattern reduction fractional ratio.
     */
    @Override
    public double evaluate(Solution solution) {
        double ratio = 0;

        int activePatternsCount = solution.getActivePatternsCount();
        if (activePatternsCount > 1) {
            ratio = (solution.getUniquePatternsCount() - 1) / (double) (activePatternsCount - 1);
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
