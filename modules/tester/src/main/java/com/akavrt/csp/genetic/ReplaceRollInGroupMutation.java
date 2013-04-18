package com.akavrt.csp.genetic;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.genetic.Chromosome;
import com.akavrt.csp.solver.genetic.Gene;
import com.akavrt.csp.solver.pattern.PatternGenerator;

import java.util.List;

/**
 * User: akavrt
 * Date: 17.04.13
 * Time: 00:36
 */
public class ReplaceRollInGroupMutation extends PatternBasedMutation {

    public ReplaceRollInGroupMutation(PatternGenerator generator) {
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

        double patternWidth = selectedGroup.getGene(0).getWidth(mutated.getContext());
        Roll roll = pickRoll(patternWidth, mutated);
        if (roll != null) {
            int geneIndexInGroup = rGen.nextInt(selectedGroup.size());
            Gene gene = selectedGroup.getGene(geneIndexInGroup);
            int geneIndexInChromosome = selectedGroup.getGeneIndex(geneIndexInGroup);

            int[] pattern = gene.getPattern().clone();

            Gene replacement = new Gene(pattern, roll);

            mutated.setGene(geneIndexInChromosome, replacement);
        }

        return mutated;
    }

}

