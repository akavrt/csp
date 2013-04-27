package com.akavrt.csp.solver.genetic.backup;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.Gene;
import com.akavrt.csp.solver.evo.operators.GeneGroup;
import com.akavrt.csp.solver.evo.operators.PatternBasedMutation;
import com.akavrt.csp.solver.pattern.PatternGenerator;

import java.util.List;

/**
 * User: akavrt
 * Date: 16.04.13
 * Time: 22:45
 */
public class GroupReplaceRollKeepPatternMutation extends PatternBasedMutation {

    public GroupReplaceRollKeepPatternMutation(PatternGenerator generator) {
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
