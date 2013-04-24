package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.solver.genetic.Chromosome;
import com.akavrt.csp.solver.genetic.GeneticExecutionContext;
import com.akavrt.csp.solver.genetic.GeneticOperator;

import java.util.Random;

/**
 * User: akavrt
 * Date: 17.04.13
 * Time: 00:37
 */
public class DeleteRollMutation implements GeneticOperator {
    protected final Random rGen;

    public DeleteRollMutation() {
        rGen = new Random();
    }

    @Override
    public void initialize(GeneticExecutionContext context) {
        // nothing to initialize
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        final Chromosome mutated = new Chromosome(chromosomes[0]);

        if (mutated.size() > 0) {
            int index = rGen.nextInt(mutated.size());
            mutated.removeGene(index);
        }

        return mutated;
    }

}

