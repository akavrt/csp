package com.akavrt.csp.solver.local;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.Gene;
import com.akavrt.csp.solver.evo.operators.GeneGroup;
import com.akavrt.csp.solver.evo.operators.PatternBasedMutation;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * User: akavrt
 * Date: 24.04.13
 * Time: 17:28
 */
public class SearchStepOperator extends PatternBasedMutation {
    private final Metric objectiveFunction;

    public SearchStepOperator(PatternGenerator generator, Metric objectiveFunction) {
        super(generator);
        this.objectiveFunction = objectiveFunction;
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        Chromosome original = new Chromosome(chromosomes[0]);
        Chromosome result = null;

        // remove each group and repair solution using sequential procedure
        // (neighbourhood is defined on groups)
        if (original.size() > 0) {
            double toleranceRatio = getWidthToleranceRatio(original);

            List<GeneGroup> groups = groupGenes(original);

            List<Chromosome> neighbours = Lists.newArrayList();
            int groupIndex = 0;
            while (groupIndex < groups.size() && !original.getContext().isCancelled()) {
                GeneGroup group = groups.get(groupIndex);

                Chromosome neighbour = new Chromosome(original);
                for (int i = group.size() - 1; i >= 0; i--) {
                    int indexToRemove = group.getGeneIndex(i);
                    neighbour.removeGene(indexToRemove);
                }

                List<Roll> replacement = searchForGroup(neighbour, toleranceRatio);
                if (replacement != null && replacement.size() > 0) {
                    double groupTotalLength = 0;
                    double groupMinWidth = 0;

                    for (int i = 0; i < replacement.size(); i++) {
                        groupTotalLength += replacement.get(i).getLength();

                        if (i == 0 || replacement.get(i).getWidth() < groupMinWidth) {
                            groupMinWidth = replacement.get(i).getWidth();
                        }
                    }

                    int[] demand = calcDemand(groupTotalLength, neighbour);
                    int[] pattern = generator.generate(groupMinWidth, demand, toleranceRatio);
                    if (pattern != null) {
                        for (Roll roll : replacement) {
                            Gene gene = new Gene(pattern.clone(), roll);
                            neighbour.addGene(gene);
                        }
                    }
                }

                neighbours.add(neighbour);

                groupIndex++;
            }

            // select best neighbour
            for (Chromosome neighbour : neighbours) {
                if (result == null || objectiveFunction.compare(neighbour, result) > 0) {
                    result = neighbour;
                }
            }
        }

        return result != null ? result : original;
    }

    private double getResidualDemandArea(Chromosome chromosome) {
        List<Order> orders = chromosome.getContext().getProblem().getOrders();

        double area = 0;
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            double produced = chromosome.getProductionLengthForOrder(i);
            double required = order.getLength();

            if (produced < required) {
                area += (required - produced) * order.getWidth();
            }
        }

        return area;
    }

    private List<Roll> searchForGroup(Chromosome chromosome, double toleranceRatio) {
        List<Roll> group = null;

        double residualDemandArea = getResidualDemandArea(chromosome);
        if (residualDemandArea > 0) {
            List<Roll> spareRolls = getSpareRolls(chromosome);
            group = searchForGroup(residualDemandArea, spareRolls, toleranceRatio);
        }

        return group;
    }

    private List<Roll> searchForGroup(double targetGroupArea, List<Roll> rolls,
                                      double toleranceRatio) {
        List<Roll> bestGroup = null;

        double bestGroupArea = 0;
        for (int i = 0; i < rolls.size(); i++) {
            double anchorWidth = rolls.get(i).getWidth();

            List<Roll> currentGroup = Lists.newArrayList();
            double currentGroupArea = 0;

            // TODO collect all rolls suited for inclusion to the current group,
            // TODO then solve knapsack problem to get best match in terms of area
            int j = 0;
            while (j < rolls.size() && currentGroupArea < targetGroupArea) {
                Roll roll = rolls.get(j);

                double delta = 1 - anchorWidth / roll.getWidth();
                if (delta >= 0 && delta <= toleranceRatio) {
                    currentGroup.add(roll);
                    currentGroupArea += roll.getArea();
                }

                j++;
            }

            if (currentGroupArea >= targetGroupArea
                    && (bestGroup == null || currentGroupArea < bestGroupArea)) {
                bestGroup = currentGroup;
                bestGroupArea = currentGroupArea;
            }
        }

        return bestGroup;
    }

}