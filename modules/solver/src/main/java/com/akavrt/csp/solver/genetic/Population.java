package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Plan;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.Algorithm;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * <p></p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Population {
    private static final Logger LOGGER = LogManager.getLogger(Population.class);
    private final GeneticExecutionContext context;
    private final GeneticAlgorithmParameters parameters;
    private final Random rGen;
    private final Comparator<Plan> comparator;
    private List<Chromosome> chromosomes;
    private int age;

    public Population(GeneticExecutionContext context, GeneticAlgorithmParameters parameters,
                      Metric objectiveFunction) {
        this.context = context;
        this.parameters = parameters;

        rGen = new Random();
        comparator = objectiveFunction.getReverseComparator();
    }

    public int getAge() {
        return age;
    }

    public List<Solution> getSolutions() {
        List<Solution> solutions = Lists.newArrayList();

        if (chromosomes != null && chromosomes.size() > 0) {
            for (Chromosome chromosome : chromosomes) {
                solutions.add(chromosome.convert());
            }
        }

        return solutions;
    }

    public void initialize(Algorithm initializationProcedure) {
        chromosomes = Lists.newArrayList();
        age = 0;

        // fill population with solutions generated using auxiliary algorithm
        while (!context.isCancelled() && chromosomes.size() < parameters.getPopulationSize()) {
            // run auxiliary algorithm
            List<Solution> solutions = initializationProcedure.execute(context);

            if (solutions != null && solutions.size() > 0) {
                // some of the methods constructs multiple solutions in a single run
                for (Solution solution : solutions) {
                    if (solution != null) {
                        // add new chromosome:
                        // conversion from solution to chromosome is done automatically
                        chromosomes.add(new Chromosome(context, solutions.get(0)));

                        // exit early if number of the chromosomes is sufficient
                        if (chromosomes.size() == parameters.getPopulationSize()) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public void sort() {
        // TODO implement mechanism to sort population only when it needed
        // (if population structure left unchanged - no need to resort it)

        // here we are using reverse ordering: in a sorted list of chromosomes
        // the best solution found will be the first element of the list,
        // while the worst solution found will be located exactly at the end of the list
        Collections.sort(chromosomes, comparator);
    }

    /**
     * <p>ModGA generation scheme is used.</p>
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

        // replace content of the exchange list with new chromosomes
        prepareExchange(exchangeList, crossover, mutation);

        // exchange the last 'GeneticAlgorithmParameters.getExchangeSize()' chromosomes
        // (the worst fitted part of the population) with new chromosomes
        int offset = chromosomes.size() - exchangeList.size();
        for (int j = 0; j < exchangeList.size(); j++) {
            chromosomes.set(offset + j, exchangeList.get(j));
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
                // create a copy of the chromosome and apply mutation to it,
                // then replace current chromosome with mutated one inside the exchange list
                Chromosome original = exchangeList.get(i);

                Chromosome mutated = mutation.apply(original);
                exchangeList.set(i, mutated);

                i++;
            }
        }

        if (matingPool.size() % 2 > 0) {
            // randomly pick one of the mating chromosomes and apply mutation to it
            int index = rGen.nextInt(matingPool.size());
            Chromosome original = matingPool.remove(index);

            Chromosome mutated = mutation.apply(original);
            exchangeList.add(mutated);
        }

        // produce required number of new chromosomes using crossover
        while (matingPool.size() > 0) {
            int firstIndex = rGen.nextInt(matingPool.size());
            Chromosome firstParent = matingPool.remove(firstIndex);

            int secondIndex = rGen.nextInt(matingPool.size());
            Chromosome secondParent = matingPool.remove(secondIndex);

            // produce first child
            Chromosome firstChild = crossover.apply(firstParent, secondParent);
            exchangeList.add(firstChild);

            // exchange parents and produce second child
            Chromosome secondChild = crossover.apply(secondParent, firstParent);
            exchangeList.add(secondChild);
        }

        return exchangeList;
    }

}
