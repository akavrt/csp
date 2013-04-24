package com.akavrt.csp.solver.genetic.backup;

import com.akavrt.csp.solver.genetic.*;

import java.util.List;

/**
 * User: akavrt
 * Date: 23.04.13
 * Time: 01:04
 */
public class ChangeGroupPatternMutation extends GroupBasedMutation {

    @Override
    public void initialize(GeneticExecutionContext context) {
        // nothing to initialize
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        Chromosome mutated = new Chromosome(chromosomes[0]);

        if (mutated.size() > 0) {
            List<GeneGroup> groups = groupGenes(mutated);
            int groupIndex = rGen.nextInt(groups.size());
            GeneGroup selectedGroup = groups.get(groupIndex);

            int[] pattern = selectedGroup.getPattern();
            if (pattern != null) {
                int orderIndex = rGen.nextInt(pattern.length);

                double produced = mutated.getProductionLengthForOrder(orderIndex);
                double required = mutated.getContext().getOrderLength(orderIndex);

                if (produced > required) {
                    int quantity = pattern[orderIndex];

                    if (quantity > 0) {
                        pattern[orderIndex]--;

                        for (int i = 0; i < selectedGroup.size(); i++) {
                            Gene gene = selectedGroup.getGene(i);
                            gene.setPattern(pattern.clone());
                        }
                    }
                } else if (produced < required) {
                    int cuts = 0;
                    for (int quantity : pattern) {
                        cuts += quantity;
                    }

                    GeneticExecutionContext context = mutated.getContext();

                    double minRollWidth = selectedGroup.getMinRollWidth();
                    double patternWidth = selectedGroup.getPatternWidth(context);

                    double orderWidth = context.getOrderWidth(orderIndex);

                    int allowedCutsNumber = context.getProblem().getAllowedCutsNumber();
                    if (minRollWidth - patternWidth >= orderWidth
                            && (allowedCutsNumber == 0 || cuts < allowedCutsNumber)) {
                        pattern[orderIndex]++;

                        for (int i = 0; i < selectedGroup.size(); i++) {
                            Gene gene = selectedGroup.getGene(i);
                            gene.setPattern(pattern.clone());
                        }
                    }
                }
            }
        }

        return mutated;
    }

}
