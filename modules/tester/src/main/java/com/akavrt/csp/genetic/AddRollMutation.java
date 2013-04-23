package com.akavrt.csp.genetic;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.genetic.Chromosome;
import com.akavrt.csp.solver.genetic.Gene;
import com.akavrt.csp.solver.pattern.PatternGenerator;

import java.util.List;

/**
 * User: akavrt
 * Date: 17.04.13
 * Time: 00:34
 */
public class AddRollMutation extends PatternBasedMutation {

    public AddRollMutation(PatternGenerator generator) {
        super(generator);
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        final Chromosome mutated = new Chromosome(chromosomes[0]);

        if (mutated.size() > 0) {
            // add roll to one of the existing groups,
            // reuse corresponding pattern
            List<GeneGroup> groups = groupGenes(mutated);
            int groupIndex = rGen.nextInt(groups.size());
            GeneGroup selectedGroup = groups.get(groupIndex);

            double patternWidth = selectedGroup.getPatternWidth(mutated.getContext());
            Roll roll = pickRoll(patternWidth, mutated);
            if (roll != null && selectedGroup.getPattern() != null) {
                int[] pattern = selectedGroup.getPattern().clone();

                Gene gene = new Gene(pattern, roll);
                mutated.addGene(gene);
            }
        } else {
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
        }

        return mutated;
    }

}
