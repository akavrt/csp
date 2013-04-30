package com.akavrt.csp.solver.evo.operators.gene;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.Gene;
import com.akavrt.csp.solver.evo.operators.Mutation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * User: akavrt
 * Date: 30.04.13
 * Time: 13:17
 */
public class SpreadGenePatternMutation extends Mutation {
    private static final Logger LOGGER = LogManager.getLogger(SpreadGenePatternMutation.class);

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        final Chromosome mutated = new Chromosome(chromosomes[0]);

        if (mutated.size() < 2) {
            return mutated;
        }

        int firstIndex = rGen.nextInt(mutated.size());
        Gene firstGene = mutated.getGene(firstIndex);

        if (firstGene.getRoll() == null) {
            return mutated;
        }

        int secondIndex = firstIndex;
        for (int i = 0; i < mutated.size(); i++) {
            Gene candidate = mutated.getGene(i);
            if (i != firstIndex && candidate.getRoll() != null
                    && candidate.getRoll().getWidth() == firstGene.getRoll().getWidth()
                    && candidate.getPatternHashCode() != firstGene.getPatternHashCode()) {
                secondIndex = i;
                break;
            }
        }

        if (firstIndex != secondIndex) {
            // gene with suitable roll was found
            // replace pattern in the second gene
            // with pattern from the first gene
            LOGGER.debug("MTH: copying pattern from gene {} to gene {}", firstIndex, secondIndex);

            int[] pattern = firstGene.getPattern().clone();
            Roll roll = mutated.getGene(secondIndex).getRoll();

            Gene replacement = new Gene(pattern, roll);
            mutated.setGene(secondIndex, replacement);
        } else {
            // gene with suitable roll wasn't found
            // pick one of the spare rolls with suitable width,
            // replace pattern and roll in randomly selected gene
            // with pattern from the first gene and previously
            // picked spare roll, respectively
            Roll roll = pickRoll(firstGene.getRoll().getWidth(), mutated);
            if (roll != null) {
                secondIndex = rGen.nextInt(mutated.size());
                LOGGER.debug("MTH: copying pattern from gene {} to gene {}, replacing roll in it",
                             firstIndex, secondIndex);

                int[] pattern = firstGene.getPattern().clone();

                Gene replacement = new Gene(pattern, roll);
                mutated.setGene(secondIndex, replacement);
            }
        }

        return mutated;
    }
}
