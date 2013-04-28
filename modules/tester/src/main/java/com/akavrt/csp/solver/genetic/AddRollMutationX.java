package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.Gene;
import com.akavrt.csp.solver.evo.operators.PatternBasedMutation;
import com.akavrt.csp.solver.pattern.PatternGenerator;

/**
 * User: akavrt
 * Date: 28.04.13
 * Time: 00:58
 */
public class AddRollMutationX extends PatternBasedMutation {

    public AddRollMutationX(PatternGenerator generator) {
        super(generator);
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        final Chromosome mutated = new Chromosome(chromosomes[0]);

        while (mutated.size() > 0 && mutated.isOrdersFulfilled()) {
            int index = rGen.nextInt(mutated.size());
            mutated.removeGene(index);
        }

        // pick any available roll and generate new pattern
        Roll roll = pickRoll(mutated);
        if (roll != null) {
            double toleranceRatio = getWidthToleranceRatio(mutated);

            int[] demand = calcDemand(roll.getLength(), mutated);
            int[] pattern = generator.generate(roll.getWidth(), demand, toleranceRatio);

            if (pattern != null) {
                Gene gene = new Gene(pattern, roll);
                mutated.addGene(gene);
            }
        }

        return mutated;
    }

}

