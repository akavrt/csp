package com.akavrt.csp.solver.genetic.backup;

import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.Gene;
import com.akavrt.csp.solver.evo.operators.GeneGroup;
import com.akavrt.csp.solver.evo.operators.PatternBasedMutation;
import com.akavrt.csp.solver.pattern.PatternGenerator;

import java.util.List;

/**
 * User: akavrt
 * Date: 16.04.13
 * Time: 23:24
 */
public class GroupDeleteRollReplacePatternMutation extends PatternBasedMutation {

    public GroupDeleteRollReplacePatternMutation(PatternGenerator generator) {
        super(generator);
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        if (chromosomes.length < 1 || chromosomes[0] == null) {
            return null;
        }

        if (chromosomes[0].size() < 2) {
            return new Chromosome(chromosomes[0]);
        }

        Chromosome original = chromosomes[0];
        Chromosome mutated = new Chromosome(original);

        List<GeneGroup> groups = groupGenes(mutated);
        int groupIndex = rGen.nextInt(groups.size());
        GeneGroup selectedGroup = groups.get(groupIndex);

        int geneIndexInGroup = rGen.nextInt(selectedGroup.size());
        int geneIndexInChromosome = selectedGroup.getGeneIndex(geneIndexInGroup);

        mutated.removeGene(geneIndexInChromosome);
        selectedGroup.removeGene(geneIndexInGroup, true);

        if (selectedGroup.size() > 0) {
            for (int i = selectedGroup.size() - 1; i >= 0; i--) {
                int indexToRemove = selectedGroup.getGeneIndex(i);
                mutated.removeGene(indexToRemove);
            }

            int[] demand = calcDemand(selectedGroup.getTotalLength(), mutated);
            int[] pattern = generator.generate(selectedGroup.getMinRollWidth(), demand, 0.1);
            if (pattern != null) {
                for (int i = 0; i < selectedGroup.size(); i++) {
                    Gene previous = selectedGroup.getGene(i);
                    Gene replacement = new Gene(pattern.clone(), previous.getRoll());

                    int indexToAdd = selectedGroup.getGeneIndex(i);
                    mutated.addGene(indexToAdd, replacement);
                }
            } else {
                for (int i = 0; i < selectedGroup.size(); i++) {
                    Gene previous = selectedGroup.getGene(i);
                    int indexToAdd = selectedGroup.getGeneIndex(i);
                    mutated.addGene(indexToAdd, previous);
                }
            }
        }

        return mutated;
    }

}
