package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Plan;
import com.akavrt.csp.core.Problem;

/**
 * <p>This metric corresponds to the minimization of the both under and overproduction of the final
 * strip ordered.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ProductDeviationMetric extends MinimizationMetric {
    private final Problem problem;

    /**
     * <p>Creates an instance of evaluator. Can be reused to evaluate different solutions against
     * same problem provided during creation.</p>
     *
     * @param problem The problem in question.
     */
    public ProductDeviationMetric(Problem problem) {
        this.problem = problem;
    }

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
        return plan.getAverageUnderProductionRatio(problem) +
                plan.getAverageOverProductionRatio(problem);
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
