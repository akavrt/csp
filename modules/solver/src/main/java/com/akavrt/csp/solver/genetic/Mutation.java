package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * User: akavrt
 * Date: 29.03.13
 * Time: 02:10
 */
public class Mutation implements GeneticUnaryOperator {
    private final PatternGenerator generator;
    private final Random rGen;

    public Mutation(PatternGenerator generator) {
        this.generator = generator;
        rGen = new Random();
    }

    @Override
    public Chromosome apply(Chromosome chromosome) {
        if (chromosome == null) {
            return null;
        }

        double draw = rGen.nextDouble();
        return draw < 0.5 ? useExistingRoll(chromosome) : useNewRoll(chromosome);
    }

    private Chromosome useExistingRoll(Chromosome chromosome) {
        Chromosome mutated = new Chromosome(chromosome);

        int index = rGen.nextInt(mutated.size());
        Roll roll = mutated.removeGene(index).getRoll();

        if (roll != null && getRatio(mutated) > getRatio(chromosome)) {
            // calculate remaining demands for orders' length
            int[] demand = calcDemand(roll, mutated);

            // generate new pattern
            int[] pattern = generator.generate(roll.getWidth(), demand, 0);

            // insert new gene into mutated chromosome
            Gene replacement = new Gene(pattern, roll);
            mutated.addGene(index, replacement);
        }

        return mutated;
    }

    private Chromosome useNewRoll(Chromosome chromosome) {
        Chromosome mutated = new Chromosome(chromosome);

        int index = rGen.nextInt(mutated.size());
        mutated.removeGene(index);

        if (getRatio(mutated) > getRatio(chromosome)) {
            // pick one of the unused rolls at random
            Roll roll = pickRoll(mutated);

            if (roll != null) {
                // calculate remaining demands for orders' length
                int[] demand = calcDemand(roll, mutated);

                // generate new pattern
                int[] pattern = generator.generate(roll.getWidth(), demand, 0);

                // insert new gene into mutated chromosome
                Gene replacement = new Gene(pattern, roll);
                mutated.addGene(index, replacement);
            }
        }

        return mutated;
    }

    private double getRatio(Chromosome chromosome) {
        return chromosome.getMetricProvider().getAverageUnderProductionRatio() +
                chromosome.getMetricProvider().getAverageOverProductionRatio();
    }

    private Roll pickRoll(Chromosome chromosome) {
        // mutable list of rolls
        List<Roll> rolls = Lists.newArrayList(chromosome.getContext().getProblem().getRolls());

        Set<Integer> usedRollIds = Sets.newHashSet();
        for (Gene gene : chromosome.getGenes()) {
            if (gene.getRoll() != null) {
                usedRollIds.add(gene.getRoll().getInternalId());
            }
        }

        int i = 0;
        while (i < rolls.size()) {
            int id = rolls.get(i).getInternalId();
            if (usedRollIds.contains(id)) {
                rolls.remove(i);
            } else {
                i++;
            }
        }

        Roll picked = null;
        if (rolls.size() > 0) {
            int index = rGen.nextInt(rolls.size());
            picked = rolls.get(index);
        }

        return picked;
    }

    public int[] calcDemand(Roll roll, Chromosome chromosome) {
        List<Order> orders = chromosome.getContext().getProblem().getOrders();

        int[] demand = new int[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            double produced = chromosome.getProductionLengthForOrder(i);
            double orderLength = orders.get(i).getLength();

            if (orderLength > produced) {
                double ratio = (orderLength - produced) / roll.getLength();
                demand[i] = ratio < 1 ? 1 : (int) Math.floor(ratio);
            }
        }

        return demand;
    }

}
