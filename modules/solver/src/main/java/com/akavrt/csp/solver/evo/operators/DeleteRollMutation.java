package com.akavrt.csp.solver.evo.operators;

import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.EvolutionaryExecutionContext;
import com.akavrt.csp.solver.evo.EvolutionaryOperator;

import java.util.Random;

/**
 * User: akavrt
 * Date: 17.04.13
 * Time: 00:37
 */
public class DeleteRollMutation implements EvolutionaryOperator {
    protected final Random rGen;

    public DeleteRollMutation() {
        rGen = new Random();
    }

    @Override
    public void initialize(EvolutionaryExecutionContext context) {
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

