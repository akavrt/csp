package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.solver.pattern.PatternGenerator;

import java.util.List;

/**
 * User: akavrt
 * Date: 17.04.13
 * Time: 00:33
 */
public class AdaptGroupPatternMutation extends PatternBasedMutation {

    public AdaptGroupPatternMutation(PatternGenerator generator) {
        super(generator);
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        final Chromosome mutated = new Chromosome(chromosomes[0]);

        if (mutated.size() > 0) {
            double toleranceRatio = getWidthToleranceRatio(mutated);

            List<GeneGroup> groups = groupGenes(mutated);
            int groupIndex = rGen.nextInt(groups.size());
            GeneGroup selectedGroup = groups.get(groupIndex);

            for (int i = selectedGroup.size() - 1; i >= 0; i--) {
                int indexToRemove = selectedGroup.getGeneIndex(i);
                mutated.removeGene(indexToRemove);
            }

            int[] demand = calcDemand(selectedGroup.getTotalLength(), mutated);
            int[] pattern = generator.generate(selectedGroup.getMinRollWidth(), demand,
                                               toleranceRatio);
            if (pattern != null) {
                for (int i = 0; i < selectedGroup.size(); i++) {
                    Gene previous = selectedGroup.getGene(i);
                    Gene replacement = new Gene(pattern.clone(), previous.getRoll());

                    int indexToAdd = selectedGroup.getGeneIndex(i);
                    mutated.addGene(indexToAdd, replacement);
                }
            }
        }

        return mutated;
    }

}