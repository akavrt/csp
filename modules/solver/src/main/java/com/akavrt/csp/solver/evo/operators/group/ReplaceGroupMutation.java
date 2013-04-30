package com.akavrt.csp.solver.evo.operators.group;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.Gene;
import com.akavrt.csp.solver.evo.operators.GeneGroup;
import com.akavrt.csp.solver.evo.operators.PatternBasedMutation;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * User: akavrt
 * Date: 23.04.13
 * Time: 17:28
 */
public class ReplaceGroupMutation extends PatternBasedMutation {

    public ReplaceGroupMutation(PatternGenerator generator) {
        super(generator);
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        final Chromosome mutated = new Chromosome(chromosomes[0]);

        if (mutated.size() > 0) {
            double toleranceRatio = getWidthToleranceRatio(mutated);

            List<GeneGroup> groups = groupGenes(mutated);

            // pick group at random
            int groupIndex = rGen.nextInt(groups.size());
            GeneGroup selectedGroup = groups.get(groupIndex);

            // remove group from chromosome
            for (int i = selectedGroup.size() - 1; i >= 0; i--) {
                int indexToRemove = selectedGroup.getGeneIndex(i);
                mutated.removeGene(indexToRemove);
            }

            List<Roll> group = searchForGroup(mutated, toleranceRatio);
            if (group != null && group.size() > 0) {
                double groupTotalLength = 0;
                double groupMinWidth = 0;

                for (int i = 0; i < group.size(); i++) {
                    groupTotalLength += group.get(i).getLength();

                    if (i == 0 || group.get(i).getWidth() < groupMinWidth) {
                        groupMinWidth = group.get(i).getWidth();
                    }
                }

                int[] demand = calcDemand(groupTotalLength, mutated);
                int[] pattern = generator.generate(groupMinWidth, demand, toleranceRatio);
                if (pattern != null) {
                    for (Roll roll : group) {
                        Gene gene = new Gene(pattern.clone(), roll);
                        mutated.addGene(gene);
                    }
                }
            }
        }

        return mutated;
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