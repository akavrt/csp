package com.akavrt.csp.solver.evo.ga;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.evo.*;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;

/**
 * <p>In genetic algorithm all operators including selection, reproduction and mutation are applied
 * to chromosomes on the population level.</p>
 *
 * <p>Here we are rejecting generation scheme used in canonical genetic algorithm in favour of the
 * so called modGA - modification proposed by Michalewicz. For more information on this subject
 * check out this reference (pp. 62–65, in particular):</p>
 *
 * <p>Michalewicz Z. Genetic algorithms + data structures = evolution programs / Z. Michalewicz. —
 * 3rd ed. — Berlin [etc.] : Springer-Verlag, 1998. — 387 p.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class GeneticPopulation extends DiversePopulation {
    private static final Logger LOGGER = LogManager.getLogger(GeneticPopulation.class);
    private final GeneticAlgorithmParameters parameters;
    private final Random rGen;

    /**
     * <p>Creates empty population. Initialization of the population has to be done separately by
     * explicit call to initialize() method.</p>
     *
     * @param context           Context is needed to create context-aware chromosomes.
     * @param parameters        Parameters of genetic algorithm.
     * @param objectiveFunction Metric used to evaluate fitness and compare chromosomes with each
     *                          other.
     */
    public GeneticPopulation(EvolutionaryExecutionContext context,
                             GeneticAlgorithmParameters parameters, Metric objectiveFunction) {
        super(context, parameters, objectiveFunction);

        this.parameters = parameters;

        rGen = new Random();
    }

    /**
     * <p>ModGA generation scheme is used to produce next generation of chromosomes.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public void generation(EvolutionaryOperator... operators) {
        EvolutionaryOperator crossover = operators[0];
        EvolutionaryOperator mutation = operators[1];

        DiversityManager dm = getDiversityManager();

        incAge();

        LOGGER.debug("*** GENERATION {} ***", getAge());

        sort();

        // pick the first 'GeneticAlgorithmParameters.getExchangeSize()' chromosomes
        // (the most fitted part of the population) and use them to produce new chromosomes
        List<Chromosome> exchangeList = Lists.newArrayList();
        for (int i = 0; i < parameters.getExchangeSize(); i++) {
            exchangeList.add(chromosomes.get(i));
        }

        dm.reset();
        for (int i = 0; i < chromosomes.size() - exchangeList.size(); i++) {
            Chromosome chromosome = chromosomes.get(i);
            dm.add(chromosome);
        }

        // replace content of the exchange list with new chromosomes
        prepareExchange(exchangeList, crossover, mutation, dm);

        // exchange the last 'GeneticAlgorithmParameters.getExchangeSize()' chromosomes
        // (the worst fitted part of the population) with new chromosomes
        int offset = chromosomes.size() - exchangeList.size();
        for (int i = 0; i < exchangeList.size(); i++) {
            Chromosome chromosome = exchangeList.get(i);
            chromosomes.set(offset + i, chromosome);
        }

        LOGGER.debug("Generation #{}, diversity measure: {} unique of {} total solutions.",
                     getAge(), dm.getMeasure(), chromosomes.size());
        LOGGER.debug("Generation #{}, {} retries were done.", getAge(), dm.getRetryCount());

        if (LOGGER.isDebugEnabled()) {
            double groupLength = 0;
            for (Chromosome chromosome : chromosomes) {
                groupLength += chromosome.getMetricProvider().getAverageGroupSize();
            }

            String formatted = String.format("%.2f", groupLength / chromosomes.size());
            LOGGER.debug("Generation #{}, average group length is {}.", getAge(), formatted);
        }
    }

    private List<Chromosome> prepareExchange(List<Chromosome> exchangeList,
                                             EvolutionaryOperator crossover,
                                             EvolutionaryOperator mutation,
                                             DiversityManager dm) {
        // let's produce exactly GeneticAlgorithmParameters.getExchangeSize()
        // new chromosomes iteratively applying crossover and mutation operators
        List<Chromosome> matingPool = Lists.newArrayList();
        int i = 0;
        while (i < exchangeList.size()) {
            double draw = rGen.nextDouble();
            if (draw < parameters.getCrossoverRate()) {
                Chromosome parent = exchangeList.remove(i);
                matingPool.add(parent);
            } else {
                i++;
            }
        }

        if (matingPool.size() % 2 > 0) {
            // randomly pick one of the mating chromosomes and move it to mutation candidates
            int index = rGen.nextInt(matingPool.size());
            exchangeList.add(matingPool.remove(index));
        }

        for (int j = 0; j < exchangeList.size(); j++) {
            // pick chromosome from the exchange list and apply mutation to it,
            // then replace original chromosome with mutated one in the exchange list
            Chromosome original = exchangeList.get(j);
            Chromosome mutated = applyMutation(mutation, dm, original);
            exchangeList.set(j, mutated);
            dm.add(mutated);
        }

        // produce required number of new chromosomes using crossover
        while (matingPool.size() > 0) {
            int firstIndex = rGen.nextInt(matingPool.size());
            Chromosome firstParent = matingPool.remove(firstIndex);

            int secondIndex = rGen.nextInt(matingPool.size());
            Chromosome secondParent = matingPool.remove(secondIndex);

            Chromosome firstChild = applyCrossover(crossover, dm, firstParent, secondParent);
            exchangeList.add(firstChild);
            dm.add(firstChild);

            Chromosome secondChild = applyCrossover(crossover, dm, secondParent, firstParent);
            exchangeList.add(secondChild);
            dm.add(secondChild);
        }

        return exchangeList;
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

    private Chromosome applyCrossover(EvolutionaryOperator crossover, DiversityManager dm,
                                      Chromosome firstParent, Chromosome secondParent) {
        int stuck = 0;
        Chromosome child = null;
        while (child == null) {
            Chromosome candidate = crossover.apply(firstParent, secondParent);
            if (dm.isAdded(candidate) && stuck < getRetryBound()) {
                dm.incRetryCount();
                stuck++;
            } else {
                child = candidate;
            }
        }

        return child;
    }

}
