package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Solution;

/**
 * <p>This class defines ordering of solutions in minimization problems. Should be used as a parent
 * class for metrics corresponding to minimization problems.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public abstract class MinimizationMetric implements Metric {
    /**
     * <p>We are dealing with minimization problem: given two solutions, solution with smaller
     * value of objective function will be better.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public int compare(Solution s1, Solution s2) {
        double s1eval = evaluate(s1);
        double s2eval = evaluate(s2);

        return s1eval > s2eval ? -1 : (s1eval < s2eval ? 1 : 0);
    }

}
