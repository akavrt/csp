package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * User: akavrt
 * Date: 25.04.13
 * Time: 00:42
 */
public class LocalSearchOperator implements GeneticOperator {
    private static final Logger LOGGER = LogManager.getLogger(LocalSearchOperator.class);
    private final Metric objectiveFunction;
    private final GeneticOperator searchStep;

    public LocalSearchOperator(PatternGenerator generator, Metric objectiveFunction) {
        this.objectiveFunction = objectiveFunction;

        searchStep = new GroupReplacementOperator(generator, objectiveFunction);
    }

    @Override
    public void initialize(GeneticExecutionContext context) {
        searchStep.initialize(context);
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        Chromosome best = new Chromosome(chromosomes[0]);
        GeneticExecutionContext context = best.getContext();

        int step = 0;
        int stuck = 0;
        while (!context.isCancelled() && step < 100 && stuck < 10) {
            if (LOGGER.isDebugEnabled()) {
                String formatted = String.format("%.3f", objectiveFunction.evaluate(best));
                LOGGER.debug("Local search step #{}, best is {}", step, formatted);
            }

            Chromosome current = searchStep.apply(best);

            if (objectiveFunction.compare(current, best) > 0) {
                best = current;
                stuck = 0;
            } else {
                LOGGER.debug("Local search stuck on step #{}", step);
                stuck++;
            }

            step++;
        }

        return best;
    }
}
