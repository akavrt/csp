package com.akavrt.csp.core;

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
}
