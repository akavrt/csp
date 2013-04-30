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
 * Date: 28.04.13
 * Time: 23:47
 */
public class MoveRollMutation extends PatternBasedMutation {

    public MoveRollMutation(PatternGenerator generator) {
        super(generator);
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        final Chromosome mutated = new Chromosome(chromosomes[0]);

        if (mutated.size() == 0) {
            return mutated;
        }

        List<GeneGroup> groups = groupGenes(mutated);

        // randomly pick one of the existing groups
        int groupIndex = rGen.nextInt(groups.size());
        GeneGroup firstGroup = groups.remove(groupIndex);

        // remove one of the genes, keep pattern unchanged
        int geneIndexInGroup = rGen.nextInt(firstGroup.size());
        int geneIndexInChromosome = firstGroup.getGeneIndex(geneIndexInGroup);

        Gene removedGene = mutated.removeGene(geneIndexInChromosome);

        Roll roll = removedGene.getRoll();
        if (roll != null && groups.size() > 0) {
            // try to relocate spare (recently removed) roll to another group
            GeneGroup best = null;
            double bestDelta = 0;
            for (GeneGroup candidate : groups) {
                double groupWidth = candidate.getMinRollWidth();
                double delta = roll.getWidth() - groupWidth;

                if (delta >= 0 && (best == null || delta < bestDelta)) {
                    best = candidate;
                    bestDelta = delta;
                }
            }

            if (best != null && best.getPattern() != null) {
                int[] pattern = best.getPattern().clone();
                Gene gene = new Gene(pattern, roll);
                mutated.addGene(gene);

                /*
                double toleranceRatio = getWidthToleranceRatio(mutated);

                // remove group's genes from chromosome
                for (int i = best.size() - 1; i >= 0; i--) {
                    int indexToRemove = best.getGeneIndex(i);
                    if (indexToRemove > geneIndexInChromosome) {
                        indexToRemove--;
                    }

                    mutated.removeGene(indexToRemove);
                }

                // reformulate residual demands
                double updatedGroupLength = best.getTotalLength() + roll.getLength();
                int[] demand = calcDemand(updatedGroupLength, mutated);

                // generate new pattern
                int[] pattern = generator.generate(best.getMinRollWidth(), demand, toleranceRatio);
                if (pattern != null) {
                    // insert group's genes with new pattern
                    for (int i = 0; i < best.size(); i++) {
                        Gene previous = best.getGene(i);
                        Gene replacement = new Gene(pattern.clone(), previous.getRoll());

                        mutated.addGene(replacement);
                    }

                    // gene with moved roll
                    Gene gene = new Gene(pattern.clone(), roll);
                    mutated.addGene(gene);
                }
                */
            }
        }

        return mutated;
    }
}
