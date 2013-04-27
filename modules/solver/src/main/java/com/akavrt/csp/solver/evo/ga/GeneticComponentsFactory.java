package com.akavrt.csp.solver.evo.ga;

import com.akavrt.csp.solver.evo.EvolutionaryComponentsFactory;
import com.akavrt.csp.solver.evo.EvolutionaryOperator;

/**
 * <p>Factory provides concrete implementations for all base components of the genetic algorithm.
 * These components may include genetic operators like crossover and mutation, procedure employed
 * to generate initial population, etc.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface GeneticComponentsFactory extends EvolutionaryComponentsFactory {
    /**
     * <p>Provides concrete implementation of the crossover operator.</p>
     *
     * @return Implementation of the crossover operator.
     */
    EvolutionaryOperator createCrossover();

}
