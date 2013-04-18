package com.akavrt.csp.genetic;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.genetic.Chromosome;
import com.akavrt.csp.solver.genetic.Gene;
import com.akavrt.csp.solver.pattern.PatternGenerator;

import java.util.List;

/**
 * User: akavrt
 * Date: 16.04.13
 * Time: 23:01
 */
public class GroupReplaceRollReplacePatternMutation extends PatternBasedMutation {

    public GroupReplaceRollReplacePatternMutation(PatternGenerator generator) {
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

            Gene replacementWithNewRoll = new Gene(pattern, roll);
            selectedGroup.setGene(geneIndexInGroup, replacementWithNewRoll);

            mutated.setGene(geneIndexInChromosome, replacementWithNewRoll);

            for (int i = selectedGroup.size() - 1; i >= 0; i--) {
                int indexToRemove = selectedGroup.getGeneIndex(i);
                mutated.removeGene(indexToRemove);
            }

            int[] demand = calcDemand(selectedGroup.getTotalLength(), mutated);
            int[] newPattern = generator.generate(selectedGroup.getMinWidth(), demand, 0.1);
            if (newPattern != null) {
                for (int i = 0; i < selectedGroup.size(); i++) {
                    Gene previous = selectedGroup.getGene(i);
                    Gene replacementWithNewPattern = new Gene(newPattern.clone(), previous.getRoll());

                    int indexToAdd = selectedGroup.getGeneIndex(i);
                    mutated.addGene(indexToAdd, replacementWithNewPattern);
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

