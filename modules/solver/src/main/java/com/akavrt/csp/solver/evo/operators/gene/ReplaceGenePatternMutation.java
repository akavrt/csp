package com.akavrt.csp.solver.evo.operators.gene;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.Gene;
import com.akavrt.csp.solver.evo.operators.PatternBasedMutation;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * User: akavrt
 * Date: 30.04.13
 * Time: 13:10
 */
public class ReplaceGenePatternMutation extends PatternBasedMutation {
    private static final Logger LOGGER = LogManager.getLogger(ReplaceGenePatternMutation.class);

    public ReplaceGenePatternMutation(PatternGenerator generator) {
        super(generator);
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        final Chromosome original = chromosomes[0];
        final Chromosome mutated = new Chromosome(original);

        int index = rGen.nextInt(mutated.size());
        LOGGER.debug("MT: remove gene {}/{}", index + 1, mutated.size());

        Roll roll = mutated.removeGene(index).getRoll();

        if (roll != null && getRatio(mutated) > getRatio(original)) {
            // calculate residual demands for orders' length
            int[] demand = calcDemand(roll.getLength(), mutated);

            // generate new pattern
            // TODO move allowed trim ratio to algorithm parameters
            int[] pattern = generator.generate(roll.getWidth(), demand, 0.1);

            // insert new gene into mutated chromosome
            Gene replacement = new Gene(pattern, roll);
            mutated.addGene(index, replacement);

            LOGGER.debug("MT: insert gene {}/{} (existing roll, new pattern)", index + 1,
                         mutated.size());
        }

        return mutated;
    }

    private double getRatio(Chromosome chromosome) {
        return chromosome.getMetricProvider().getAverageUnderProductionRatio() +
                chromosome.getMetricProvider().getAverageOverProductionRatio();
    }

}
