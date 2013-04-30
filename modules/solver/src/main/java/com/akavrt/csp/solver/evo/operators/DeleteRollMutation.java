package com.akavrt.csp.solver.evo.operators;

import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.Gene;
import com.akavrt.csp.solver.pattern.PatternGenerator;

import java.util.List;

/**
 * User: akavrt
 * Date: 17.04.13
 * Time: 00:37
 */
public class DeleteRollMutation extends PatternBasedMutation {

    public DeleteRollMutation(PatternGenerator generator) {
        super(generator);
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        final Chromosome mutated = new Chromosome(chromosomes[0]);

        if (mutated.size() > 0) {
            double toleranceRatio = getWidthToleranceRatio(mutated);

            // delete roll from one of the existing groups,
            // formulate residual demands (without group that was updated)
            // and generate new pattern for updated group
            List<GeneGroup> groups = groupGenes(mutated);
            int groupIndex = rGen.nextInt(groups.size());
            GeneGroup selectedGroup = groups.get(groupIndex);

            // remove group's genes from chromosome
            for (int i = selectedGroup.size() - 1; i >= 0; i--) {
                int indexToRemove = selectedGroup.getGeneIndex(i);
                mutated.removeGene(indexToRemove);
            }

            int geneIndexInGroup = rGen.nextInt(selectedGroup.size());
            selectedGroup.removeGene(geneIndexInGroup, true);

            if (selectedGroup.size() > 0) {
                // formulate residual demands
                int[] demand = calcDemand(selectedGroup.getTotalLength(), mutated);

                // generate new pattern
                int[] pattern = generator.generate(selectedGroup.getMinRollWidth(), demand,
                                                   toleranceRatio);
                if (pattern != null) {
                    // insert group's genes with new pattern
                    for (int i = 0; i < selectedGroup.size(); i++) {
                        Gene previous = selectedGroup.getGene(i);
                        Gene replacement = new Gene(pattern.clone(), previous.getRoll());

                        int indexToAdd = selectedGroup.getGeneIndex(i);
                        mutated.addGene(indexToAdd, replacement);
                    }
                }
            }
        }

        return mutated;
    }

}

