package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.solver.Algorithm;

/**
 * <p>Factory provides concrete implementations for all base components of the genetic algorithm.
 * These components may include genetic operators like crossover and mutation, procedure employed
 * to generate initial population, etc..</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface GeneticComponentsFactory {
    /**
     * <p>Provides concrete implementation of the crossover operator.</p>
     *
     * @return Implementation of the crossover operator.
     */
    GeneticOperator createCrossover();

    /**
     * <p>Provides concrete implementation of the mutation operator.</p>
     *
     * @return Implementation of the mutation operator.
     */
    GeneticOperator createMutation();

    /**
     * <p>In genetic algorithms initial population is filled in with chromosomes generated randomly
     * or constructed using some helper method.</p>
     *
     * <p>Existing Algorithm interface is used to configure genetic algorithm with implementation
     * of a method in charge of filling initial population.</p>
     *
     * @return Algorithm using to generate solutions which will be converted to chromosomes and
     *         placed into initial population.
     */
    Algorithm createInitializationProcedure();
}
