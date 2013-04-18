package com.akavrt.csp.genetic;

import com.akavrt.csp.solver.genetic.Chromosome;
import com.akavrt.csp.solver.pattern.PatternGenerator;

import java.util.List;

/**
 * User: akavrt
 * Date: 17.04.13
 * Time: 00:37
 */
public class DeleteRollFromGroupMutation extends PatternBasedMutation {

    public DeleteRollFromGroupMutation(PatternGenerator generator) {
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
        Chromosome mutated = new Chromosome(original);

        List<GeneGroup> groups = groupGenes(mutated);
        int groupIndex = rGen.nextInt(groups.size());
        GeneGroup selectedGroup = groups.get(groupIndex);

        int geneIndexInGroup = rGen.nextInt(selectedGroup.size());
        int geneIndexInChromosome = selectedGroup.getGeneIndex(geneIndexInGroup);

        mutated.removeGene(geneIndexInChromosome);

        return mutated;
    }

}

