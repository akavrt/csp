package com.akavrt.csp.solver.evo;

import com.akavrt.csp.solver.Algorithm;

/**
 * <p>Factory provides concrete implementations for all base components of the evolutionary
 * algorithm. These components include mutation operator and procedure employed to generate initial
 * population.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface EvolutionaryComponentsFactory {
    /**
     * <p>Provides concrete implementation of the mutation operator.</p>
     *
     * @return Implementation of the mutation operator.
     */
    EvolutionaryOperator createMutation();

    /**
     * <p>In evolutionary algorithms initial population is filled in with chromosomes generated
     * randomly or constructed using some helper method.</p>
     *
     * <p>Existing Algorithm interface is used to configure evolutionary algorithm with
     * implementation of a method in charge of filling initial population.</p>
     *
     * @return Algorithm using to generate solutions which will be converted to chromosomes and
     *         placed into initial population.
     */
    Algorithm createInitializationProcedure();

}
