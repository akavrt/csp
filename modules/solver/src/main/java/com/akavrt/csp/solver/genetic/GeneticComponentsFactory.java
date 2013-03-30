package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.solver.Algorithm;

/**
 * User: akavrt
 * Date: 30.03.13
 * Time: 23:54
 */
public interface GeneticComponentsFactory {
    GeneticBinaryOperator createCrossoverOperator();
    GeneticUnaryOperator createMutationOperator();
    Algorithm createInitializationProcedure();
}
