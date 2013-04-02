package com.akavrt.csp.solver.genetic;

/**
 * <p>This interface defines common contract for genetic operators.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface GeneticOperator {
    /**
     * <p>Additional initialization may be required for some implementations of genetic operators.
     * In particular, this is the case when single instance of the operator is used to solve
     * different problems - components like pattern generators in general depends on data contained
     * in a problem definition and have to be reinitialized accordingly.</p>
     *
     * @param context The context containing data specific to the current execution of the
     *                algorithm.
     */
    void initialize(GeneticExecutionContext context);

    /**
     * <p>Applies operator to the operands from the given list of chromosomes and produces result.
     * In general result should be represented by a new instance of Chromosome and operands left
     * untouched.</p>
     *
     * @param chromosomes List of operands.
     * @return The resulting chromosome.
     */
    Chromosome apply(Chromosome... chromosomes);
}
