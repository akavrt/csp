package com.akavrt.csp.genetic;

import com.akavrt.csp.solver.genetic.Chromosome;
import com.akavrt.csp.solver.pattern.PatternGenerator;

import java.util.List;

/**
 * User: akavrt
 * Date: 16.04.13
 * Time: 23:12
 */
public class GroupDeleteRollKeepPatternMutation extends PatternBasedMutation {

    public GroupDeleteRollKeepPatternMutation(PatternGenerator generator) {
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

        return mutated;
    }

}

