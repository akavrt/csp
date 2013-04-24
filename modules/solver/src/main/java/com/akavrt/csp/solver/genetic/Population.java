package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Plan;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.Algorithm;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

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
public class Population {
    private static final Logger LOGGER = LogManager.getLogger(Population.class);
    private static final int RETRY_BOUND = 10;
    private final GeneticExecutionContext context;
    private final GeneticAlgorithmParameters parameters;
    private final Random rGen;
    private final Comparator<Plan> comparator;
    private final List<Chromosome> chromosomes;
    private final Set<Integer> hashes;
    private int age;
    private GeneticProgressChangeListener progressChangeListener;
    private int retryCount;

    /**
     * <p>Creates empty population. Initialization of the population has to be done separately by
     * explicit call to initialize() method.</p>
     *
     * @param context           Context is needed to create context-aware chromosomes.
     * @param parameters        Parameters of genetic algorithm.
     * @param objectiveFunction Metric used to evaluate fitness and compare chromosomes with each
     *                          other.
     */
    public Population(GeneticExecutionContext context, GeneticAlgorithmParameters parameters,
                      Metric objectiveFunction) {
        this.context = context;
        this.parameters = parameters;

        chromosomes = Lists.newArrayList();
        hashes = Sets.newHashSet();

        rGen = new Random();
        comparator = objectiveFunction.getReverseComparator();
    }

    /**
     * <p>Age of the population is defined as a number of full generation cycles the population has
     * undergone since last initialization.</p>
     *
     * @return Age of the population.
     */
    public int getAge() {
        return age;
    }

    /**
     * <p>Converts each chromosome stored within population into corresponding instance of Solution
     * and returns them in a list.</p>
     *
     * @return Content of the population converted into the list of solutions.
     */
    public List<Solution> getSolutions() {
        List<Solution> solutions = Lists.newArrayList();

        if (chromosomes != null && chromosomes.size() > 0) {
            for (Chromosome chromosome : chromosomes) {
                solutions.add(chromosome.convert());
            }
        }

        return solutions;
    }

    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    public void setProgressChangeListener(GeneticProgressChangeListener listener) {
        this.progressChangeListener = listener;
    }

    /**
     * <p>Fills in population with chromosomes obtained with a help of auxiliary algorithm and
     * resets age counter to zero.</p>
     *
     * @param initializationProcedure Algorithm used to construct solutions. Conversion to
     *                                chromosomes is handled by the population itself.
     */
    public void initialize(Algorithm initializationProcedure) {
        chromosomes.clear();
        hashes.clear();

        age = 0;

        // fill population with solutions generated using auxiliary algorithm
        int stuck = 0;
        while (!context.isCancelled() && chromosomes.size() < parameters.getPopulationSize()) {
            // run auxiliary algorithm
            List<Solution> solutions = initializationProcedure.execute(context);

            if (solutions != null && solutions.size() > 0 && solutions.get(0) != null) {
                // conversion from solution to chromosome is done automatically
                Chromosome chromosome = new Chromosome(context, solutions.get(0));
                int hash = chromosome.hashCode();
                if (hashes.contains(hash) && stuck < RETRY_BOUND) {
                    stuck++;
                } else {
                    chromosomes.add(chromosome);
                    hashes.add(hash);

                    stuck = 0;

                    if (progressChangeListener != null) {
                        int progress = 100 * chromosomes.size() / parameters.getPopulationSize();
                        progress = Math.min(progress, 100);

                        progressChangeListener.onInitializationProgressChanged(progress);
                    }
                }
            }
        }
    }

    /**
     * <p>On the population level of genetic algorithm we are using the following ordering: in a
     * sorted list of chromosomes the best solution found will be the first element of the list,
     * while the worst solution found will be located exactly at the end of the list.</p>
     */
    public void sort() {
        Collections.sort(chromosomes, comparator);
    }

    /**
     * <p>ModGA generation scheme is used to produce next generation of chromosomes.</p>
     *
     * @param crossover Implementation of crossover operator.
     * @param mutation  Implementation of mutation operator.
     */
    public void generation(GeneticOperator crossover, GeneticOperator mutation) {
        age++;
        LOGGER.debug("*** GENERATION {} ***", age);

        sort();

        // pick the first 'GeneticAlgorithmParameters.getExchangeSize()' chromosomes
        // (the most fitted part of the population) and use them to produce new chromosomes
        List<Chromosome> exchangeList = Lists.newArrayList();
        for (int i = 0; i < parameters.getExchangeSize(); i++) {
            exchangeList.add(chromosomes.get(i));
        }

        hashes.clear();
        for (int i = 0; i < chromosomes.size() - exchangeList.size(); i++) {
            int hash = chromosomes.get(i).hashCode();
            hashes.add(hash);
        }

        retryCount = 0;

        // replace content of the exchange list with new chromosomes
        prepareExchange(exchangeList, crossover, mutation);

        // exchange the last 'GeneticAlgorithmParameters.getExchangeSize()' chromosomes
        // (the worst fitted part of the population) with new chromosomes
        int offset = chromosomes.size() - exchangeList.size();
        for (int i = 0; i < exchangeList.size(); i++) {
            Chromosome chromosome = exchangeList.get(i);
            chromosomes.set(offset + i, chromosome);
        }

        LOGGER.debug("Population age is {}, diversity measure: {} unique of {} total solutions.",
                     age, hashes.size(), chromosomes.size());
        LOGGER.debug("Population age is {}, {} retries were done during generation.", age,
                     retryCount);

        if (LOGGER.isDebugEnabled()) {
            double groupLength = 0;
            for (Chromosome chromosome : chromosomes) {
                groupLength += chromosome.getMetricProvider().getAverageGroupSize();
            }

            String formatted = String.format("%.2f", groupLength / chromosomes.size());
            LOGGER.debug("Population age is {}, average group length is {}", age, formatted);
        }
    }

    public void localSearch(GeneticOperator localSearchOperator) {
        localSearch(localSearchOperator, 1);
    }

    public void localSearch(GeneticOperator localSearchOperator, int count) {
        sort();

        int countToApply = Math.min(count, chromosomes.size());
        for (int i = 0; i < countToApply; i++) {
            Chromosome current = chromosomes.get(i);
            Chromosome refined = localSearchOperator.apply(current);
            chromosomes.set(i, refined);
        }
    }

    private List<Chromosome> prepareExchange(List<Chromosome> exchangeList,
                                             GeneticOperator crossover,
                                             GeneticOperator mutation) {
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
            Chromosome mutated = applyMutation(mutation, original);
            exchangeList.set(j, mutated);
            hashes.add(mutated.hashCode());
        }

        // produce required number of new chromosomes using crossover
        while (matingPool.size() > 0) {
            int firstIndex = rGen.nextInt(matingPool.size());
            Chromosome firstParent = matingPool.remove(firstIndex);

            int secondIndex = rGen.nextInt(matingPool.size());
            Chromosome secondParent = matingPool.remove(secondIndex);

            Chromosome firstChild = applyCrossover(crossover, firstParent, secondParent);
            exchangeList.add(firstChild);
            hashes.add(firstChild.hashCode());

            Chromosome secondChild = applyCrossover(crossover, secondParent, firstParent);
            exchangeList.add(secondChild);
            hashes.add(secondChild.hashCode());
        }

        return exchangeList;
    }

    private Chromosome applyMutation(GeneticOperator mutation, Chromosome original) {
        int stuck = 0;
        Chromosome mutated = null;
        while (mutated == null) {
            Chromosome candidate = mutation.apply(original);
            int hash = candidate.hashCode();
            if (hashes.contains(hash) && stuck < RETRY_BOUND) {
                retryCount++;
                stuck++;
            } else {
                mutated = candidate;
            }
        }

        return mutated;
    }

    private Chromosome applyCrossover(GeneticOperator crossover,
                                      Chromosome firstParent, Chromosome secondParent) {
        int stuck = 0;
        Chromosome child = null;
        while (child == null) {
            Chromosome candidate = crossover.apply(firstParent, secondParent);
            int hash = candidate.hashCode();
            if (hashes.contains(hash) && stuck < RETRY_BOUND) {
                retryCount++;
                stuck++;
            } else {
                child = candidate;
            }
        }

        return child;
    }

}
