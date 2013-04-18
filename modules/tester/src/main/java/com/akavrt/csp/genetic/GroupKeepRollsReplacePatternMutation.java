package com.akavrt.csp.genetic;

import com.akavrt.csp.solver.genetic.Chromosome;
import com.akavrt.csp.solver.genetic.Gene;
import com.akavrt.csp.solver.pattern.PatternGenerator;

import java.util.List;

/**
 * User: akavrt
 * Date: 16.04.13
 * Time: 21:12
 */
public class GroupKeepRollsReplacePatternMutation extends PatternBasedMutation {

    public GroupKeepRollsReplacePatternMutation(PatternGenerator generator) {
        super(generator);
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        if (chromosomes.length < 1 || chromosomes[0] == null) {
            return null;
        }

        if (chromosomes[0].size() == 0) {
            return new Chromosome(chromosomes[0]);
        }

        Chromosome original = chromosomes[0];
        List<GeneGroup> groups = groupGenes(original);
        int groupIndex = rGen.nextInt(groups.size());
        GeneGroup selectedGroup = groups.get(groupIndex);

        Chromosome mutated = new Chromosome(original);
        for (int i = selectedGroup.size() - 1; i >= 0; i--) {
            int indexToRemove = selectedGroup.getGeneIndex(i);
            mutated.removeGene(indexToRemove);
        }

        int[] demand = calcDemand(selectedGroup.getTotalLength(), mutated);
        int[] pattern = generator.generate(selectedGroup.getMinWidth(), demand, 0.1);
        if (pattern != null) {
            for (int i = 0; i < selectedGroup.size(); i++) {
                Gene previous = selectedGroup.getGene(i);
                Gene replacement = new Gene(pattern.clone(), previous.getRoll());

                int indexToAdd = selectedGroup.getGeneIndex(i);
                mutated.addGene(indexToAdd, replacement);
            }
        } else {
            mutated = new Chromosome(original);
        }

        return mutated;
    }

}
