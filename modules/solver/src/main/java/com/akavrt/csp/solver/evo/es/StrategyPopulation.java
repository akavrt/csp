package com.akavrt.csp.solver.evo.es;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.evo.*;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;

/**
 * <p>Implementation of Population with gene diversity management used in evolution strategy.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class StrategyPopulation extends DiversePopulation {
    private static final Logger LOGGER = LogManager.getLogger(StrategyPopulation.class);
    private static final boolean IS_MU_PLUS_LAMBDA = true;
    private final EvolutionStrategyParameters parameters;
    private final Metric objectiveFunction;
    private final List<Chromosome> offspringChromosomes;
    private final Random rGen;

    /**
     * <p>Creates empty population. Initialization of the population has to be done separately by
     * explicit call to initialize() method.</p>
     *
     * @param context           Context is needed to create context-aware chromosomes.
     * @param parameters        Parameters of evolution strategy.
     * @param objectiveFunction Metric used to evaluate fitness and compare chromosomes with each
     *                          other.
     */
    public StrategyPopulation(EvolutionaryExecutionContext context,
                              EvolutionStrategyParameters parameters, Metric objectiveFunction) {
        super(context, parameters, objectiveFunction);

        this.parameters = parameters;
        this.objectiveFunction = objectiveFunction;

        offspringChromosomes = Lists.newArrayList();
        rGen = new Random();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generation(EvolutionaryOperator... operators) {
        EvolutionaryOperator mutation = operators[0];

        DiversityManager dm = getDiversityManager();

        incAge();

        LOGGER.debug("*** GENERATION {} ***", getAge());

        dm.reset();

        if (IS_MU_PLUS_LAMBDA) {
            // IMPORTANT used only by (mu + lambda)-ES
            // content of the parent population lately will be added
            // to the intermediate population to compete with offsprings
            // register all parent chromosomes within diversity manager
            for (Chromosome chromosome : chromosomes) {
                dm.add(chromosome);
            }

            LOGGER.debug("Generation #{}, diversity measure: {} unique of {} total solutions.",
                         getAge(), dm.getMeasure(), chromosomes.size());
        }

        offspringChromosomes.clear();
        while (!getContext().isCancelled()
                && offspringChromosomes.size() < parameters.getOffspringCount()) {
            // select parent using tournament selection
            Chromosome original = getParent();

            Chromosome mutated = applyMutation(mutation, dm, original);
            offspringChromosomes.add(mutated);
            dm.add(mutated);
        }

        LOGGER.debug("Generation #{}, {} retries were done.", getAge(), dm.getRetryCount());

        if (IS_MU_PLUS_LAMBDA) {
            // IMPORTANT used only by (mu + lambda)-ES
            // add parents to intermediate population
            offspringChromosomes.addAll(chromosomes);
        }

        // sort intermediate population
        sort(offspringChromosomes);

        // truncate intermediate population to create new parent population
        chromosomes.clear();
        for (int i = 0; i < parameters.getPopulationSize(); i++) {
            Chromosome chromosome = offspringChromosomes.get(i);
            chromosomes.add(chromosome);
        }

        if (LOGGER.isDebugEnabled()) {
            double groupLength = 0;
            for (Chromosome chromosome : chromosomes) {
                groupLength += chromosome.getMetricProvider().getAverageGroupSize();
            }

            String formatted = String.format("%.2f", groupLength / chromosomes.size());
            LOGGER.debug("Generation #{}, average group length is {}.", getAge(), formatted);
        }
    }

    private Chromosome getParent() {
        int contenders = 0;
        Chromosome best = null;
        while (contenders < parameters.getTourSize()) {
            int index = rGen.nextInt(chromosomes.size());
            Chromosome contender = chromosomes.get(index);

            if (best == null || objectiveFunction.compare(contender, best) > 0) {
                best = contender;
            }

            contenders++;
        }

        return best;
    }

    private Chromosome applyMutation(EvolutionaryOperator mutation, DiversityManager dm,
                                     Chromosome original) {
        int stuck = 0;
        Chromosome mutated = null;
        while (mutated == null) {
            Chromosome candidate = mutation.apply(original);
            if (dm.isAdded(candidate) && stuck < getRetryBound()) {
                dm.incRetryCount();
                stuck++;
            } else {
                mutated = candidate;
            }
        }

        return mutated;
    }

}