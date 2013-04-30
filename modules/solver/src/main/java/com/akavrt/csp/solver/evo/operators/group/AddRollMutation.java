package com.akavrt.csp.solver.evo.operators.group;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.Gene;
import com.akavrt.csp.solver.evo.operators.GeneGroup;
import com.akavrt.csp.solver.evo.operators.PatternBasedMutation;
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
            // formulate residual demands (without group that was updated)
            // and generate new pattern for updated group
            double toleranceRatio = getWidthToleranceRatio(mutated);

            List<GeneGroup> groups = groupGenes(mutated);
            int groupIndex = rGen.nextInt(groups.size());
            GeneGroup selectedGroup = groups.get(groupIndex);

            double patternWidth = selectedGroup.getPatternWidth(mutated.getContext());
            Roll roll = pickRoll(patternWidth, mutated);
            if (roll != null) {
                // remove group's genes from chromosome
                for (int i = selectedGroup.size() - 1; i >= 0; i--) {
                    int indexToRemove = selectedGroup.getGeneIndex(i);
                    mutated.removeGene(indexToRemove);
                }

                // reformulate residual demands
                double updatedGroupLength = selectedGroup.getTotalLength() + roll.getLength();
                int[] demand = calcDemand(updatedGroupLength, mutated);

                // generate new pattern
                double updatedGroupMinWidth = Math.min(selectedGroup.getMinRollWidth(),
                                                       roll.getWidth());
                int[] pattern = generator.generate(updatedGroupMinWidth, demand, toleranceRatio);
                if (pattern != null) {
                    // insert group's genes with new pattern
                    for (int i = 0; i < selectedGroup.size(); i++) {
                        Gene previous = selectedGroup.getGene(i);
                        Gene replacement = new Gene(pattern.clone(), previous.getRoll());

                        int indexToAdd = selectedGroup.getGeneIndex(i);
                        mutated.addGene(indexToAdd, replacement);
                    }

                    // gene with new roll
                    Gene gene = new Gene(pattern.clone(), roll);
                    mutated.addGene(gene);
                }
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
