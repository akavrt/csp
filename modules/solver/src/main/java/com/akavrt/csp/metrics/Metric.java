package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Solution;

/**
 * <p>This interface is introduced to decouple solution from evaluation of the objective function.
 * We can easily implement several evaluators - each one with different objective function - and
 * evaluate same instance of Solution against them. In previous version there was no support for
 * functionality like this: objective function evaluation was tightly tied with solution, only one
 * objective function was used at a time.<p/>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface Metric {
    /**
     * <p>Calculate the value of objective function for provided solution. It's up to client to
     * decide what objective function and of which structure should be used.</p>
     *
     * @param solution The solution to evaluate.
     * @return The evaluated value of the objective function.
     */
    double evaluate(Solution solution);

    /**
     * <p>Evaluate and compare two solutions.</p>
     *
     * <p>Using this method we can hide details about type ot the optimization problem being
     * solved: whether it is a minimization or maximization problem.</p>
     *
     * @param s1 The first solution to be compared.
     * @param s2 The second solution to be compared.
     * @return A negative integer, zero, or a positive integer as the first argument is worse than,
     *         equal to, or better than the second in terms of solution quality.
     */
    int compare(Solution s1, Solution s2);

    /**
     * <p>Abbreviated name of the metric.</p>
     *
     * @return Abbreviated name of the metric.
     */
    String abbreviation();

    /**
     * <p>Human-readable name of the metric.</p>
     *
     * @return Full name of the metric.
     */
    String name();
}
