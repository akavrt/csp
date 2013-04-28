package com.akavrt.csp.solver.evo.operators;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.EvolutionaryExecutionContext;
import com.akavrt.csp.solver.evo.Gene;

import java.util.List;

/**
 * User: akavrt
 * Date: 17.04.13
 * Time: 00:36
 */
public class ReplaceRollMutation extends GroupBasedMutation {

    @Override
    public void initialize(EvolutionaryExecutionContext context) {
        // nothing to initialize
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        final Chromosome mutated = new Chromosome(chromosomes[0]);

        if (mutated.size() > 0) {
            // replace roll in one of the existing groups
            // keep corresponding pattern unchanged
            List<GeneGroup> groups = groupGenes(mutated);
            int groupIndex = rGen.nextInt(groups.size());
            GeneGroup selectedGroup = groups.get(groupIndex);

            double patternWidth = selectedGroup.getPatternWidth(mutated.getContext());
            Roll roll = pickRoll(patternWidth, mutated);
            if (roll != null && selectedGroup.getPattern() != null) {
                int geneIndexInGroup = rGen.nextInt(selectedGroup.size());
                int geneIndexInChromosome = selectedGroup.getGeneIndex(geneIndexInGroup);

                int[] pattern = selectedGroup.getPattern().clone();

                Gene replacement = new Gene(pattern, roll);

                mutated.setGene(geneIndexInChromosome, replacement);
            }
        }

        return mutated;
    }

}

