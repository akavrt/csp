package com.akavrt.csp.solver.evo.operators.group;

import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.Gene;
import com.akavrt.csp.solver.evo.operators.GeneGroup;
import com.akavrt.csp.solver.evo.operators.PatternBasedMutation;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * User: akavrt
 * Date: 22.04.13
 * Time: 19:32
 */
public class MergeTwoGroupsMutation extends PatternBasedMutation {

    public MergeTwoGroupsMutation(PatternGenerator generator) {
        super(generator);
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        final Chromosome mutated = new Chromosome(chromosomes[0]);

        if (mutated.size() == 0) {
            return mutated;
        }

        List<GeneGroup> groups = groupGenes(mutated);
        if (groups.size() > 1) {
            int groupIndex = rGen.nextInt(groups.size());
            GeneGroup firstGroup = groups.remove(groupIndex);
            double targetWidth = firstGroup.getMinRollWidth();

            double toleranceRatio = getWidthToleranceRatio(mutated);

            List<GeneGroup> candidateGroups = Lists.newArrayList();
            for (GeneGroup group : groups) {
                double groupWidth = group.getMinRollWidth();

                // TODO lower and upper bounds are parameter methods
                double lowerBound = targetWidth * (1 - toleranceRatio);
                double upperBound = targetWidth * (1 + toleranceRatio);
                if (groupWidth >= lowerBound && groupWidth <= upperBound) {
                    candidateGroups.add(group);
                }
            }

            if (candidateGroups.size() > 0) {
                // TODO we need more sophisticated approach to select right group?
                groupIndex = rGen.nextInt(candidateGroups.size());
                GeneGroup secondGroup = candidateGroups.get(groupIndex);

                removeGenes(firstGroup, secondGroup, mutated);

                // formulate residual demands
                double newGroupLength = firstGroup.getTotalLength() + secondGroup.getTotalLength();
                int[] demand = calcDemand(newGroupLength, mutated);

                // generate new pattern
                double newGroupMinWidth = Math.min(firstGroup.getMinRollWidth(),
                                                   secondGroup.getMinRollWidth());
                int[] pattern = generator.generate(newGroupMinWidth, demand, toleranceRatio);
                if (pattern != null) {
                    addGenes(firstGroup, secondGroup, pattern, mutated);
                }
            }
        }

        return mutated;
    }

    private void removeGenes(GeneGroup firstGroup, GeneGroup secondGroup, Chromosome chromosome) {
        List<Integer> indices = Lists.newArrayList();

        for (int i = 0; i < firstGroup.size(); i++) {
            int index = firstGroup.getGeneIndex(i);
            indices.add(index);
        }

        for (int i = 0; i < secondGroup.size(); i++) {
            int index = secondGroup.getGeneIndex(i);
            indices.add(index);
        }

        Collections.sort(indices);
        for (int i = indices.size() - 1; i >= 0; i--) {
            int indexToRemove = indices.get(i);
            chromosome.removeGene(indexToRemove);
        }
    }

    private void addGenes(GeneGroup firstGroup, GeneGroup secondGroup, int[] pattern,
                          Chromosome chromosome) {
        for (int i = 0; i < firstGroup.size(); i++) {
            Gene previous = firstGroup.getGene(i);
            Gene replacement = new Gene(pattern.clone(), previous.getRoll());

            chromosome.addGene(replacement);
        }

        for (int i = 0; i < secondGroup.size(); i++) {
            Gene previous = secondGroup.getGene(i);
            Gene replacement = new Gene(pattern.clone(), previous.getRoll());

            chromosome.addGene(replacement);
        }

    }

}
