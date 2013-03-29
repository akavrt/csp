package com.akavrt.csp.solver.genetic;

/**
 * User: akavrt
 * Date: 27.03.13
 * Time: 17:51
 */
public interface UnaryOperator {
    Chromosome apply(Chromosome chromosome);
}
