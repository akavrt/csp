package com.akavrt.csp.solver.evo;

import com.akavrt.csp.core.Solution;
import com.akavrt.csp.solver.Algorithm;

import java.util.List;

/**
 * <p></p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface Population {
    /**
     * <p>Age of the population is defined as a number of full generation cycles the population has
     * undergone since last initialization.</p>
     *
     * @return Age of the population.
     */
    int getAge();

    /**
     * <p>Returns list of the chromosome stored within population.</p>
     *
     * @return Content of the population.
     */
    List<Chromosome> getChromosomes();

    /**
     * <p>On the population level of evolutionary algorithm we are using the following ordering: in
     * a sorted list of chromosomes the best solution found will be the first element of the list,
     * while the worst solution found will be located exactly at the end of the list.</p>
     */
    void sort();

    /**
     * <p>Fills in population with chromosomes obtained with a help of auxiliary algorithm and
     * resets age counter to zero.</p>
     *
     * <p>If constructive algorithm can't produce sufficient number of solutions to fill in the
     * initial population, evolutionary algorithm may stuck on initialization phase.</p>
     *
     * @param initializationProcedure Algorithm used to construct solutions. Conversion to
     *                                chromosomes is handled by the population itself.
     */
    void initialize(Algorithm initializationProcedure);

    /**
     * <p>Fills in population with chromosomes obtained with a help of auxiliary algorithm and
     * resets age counter to zero.</p>
     *
     * @param initializationProcedure Algorithm used to construct solutions. Conversion to
     *                                chromosomes is handled by the population itself.
     * @param listener                An optional listener could be attached to the population to
     *                                get feedback about progress of the initialization phase.
     */
    void initialize(Algorithm initializationProcedure, EvolutionProgressChangeListener listener);

    /**
     * <p>Implements generational step used to produce next generation of chromosomes.</p>
     *
     * @param operators List of operators used by the evolutionary algorithm.
     */
    void generation(EvolutionaryOperator... operators);

    /**
     * <p>Converts each chromosome stored within population into corresponding instance of Solution
     * and returns them in a list.</p>
     *
     * @return Content of the population converted into the list of solutions.
     */
    List<Solution> getSolutions();

}