package com.akavrt.csp.solver.genetic;

/**
 * User: akavrt
 * Date: 27.03.13
 * Time: 17:52
 */
public interface BinaryOperator {
    Chromosome apply(Chromosome ch1, Chromosome ch2);
}
