package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.Algorithm;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * User: akavrt
 * Date: 27.03.13
 * Time: 16:03
 */
public class Population {
    private final GeneticExecutionContext context;
    private final GeneticAlgorithmParameters parameters;
    private final Metric objectiveFunction;
    private final Random rGen;
    private List<Chromosome> chromosomes;

    public Population(GeneticExecutionContext context, GeneticAlgorithmParameters parameters,
                      Metric objectiveFunction) {
        this.context = context;
        this.parameters = parameters;
        this.objectiveFunction = objectiveFunction;

        rGen = new Random();
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

        // fill population with solutions generated using auxiliary algorithm
        while (!context.isCancelled() && chromosomes.size() < parameters.getPopulationSize()) {
            // run auxiliary algorithm
            List<Solution> solutions = initializationProcedure.execute(context);

            if (solutions.size() > 0 && solutions.get(0) != null) {
                // add new chromosome:
                // conversion from solution to chromosome is done automatically
                chromosomes.add(new Chromosome(context, solutions.get(0)));
            }
        }
    }

    public void sort() {
        // TODO implement mechanism to sort population only when it needed
        // (if population structure left unchanged - no need to resort it)
        Collections.sort(chromosomes, new Comparator<Chromosome>() {

            @Override
            public int compare(Chromosome lhs, Chromosome rhs) {
                return objectiveFunction.compare(lhs, rhs);
            }
        });
    }

    /**
     * <p>ModGA generation scheme is used.</p>
     */
    public void generation(GeneticBinaryOperator crossover, GeneticUnaryOperator mutation) {
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
                                             GeneticBinaryOperator crossover,
                                             GeneticUnaryOperator mutation) {
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
